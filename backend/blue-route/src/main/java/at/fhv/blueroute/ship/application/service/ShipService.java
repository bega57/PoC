package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.common.websocket.SessionStatusMessage;
import at.fhv.blueroute.player.application.exception.PlayerNotFoundException;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import at.fhv.blueroute.ship.application.exception.InsufficientBalanceException;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.model.ShipType;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.repository.SessionRepository;
import at.fhv.blueroute.session.application.exception.SessionNotFoundException;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.ship.presentation.dto.BuyShipRequest;
import at.fhv.blueroute.ship.presentation.dto.SellShipRequest;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import at.fhv.blueroute.ship.application.exception.ShipOutOfStockException;
import at.fhv.blueroute.common.websocket.WebSocketSender;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipService {

    private final JpaShipRepository shipRepository;
    private final PlayerRepository playerRepository;
    private final ShipMapper shipMapper;
    private final CalculateShipSellPriceService sellPriceService;
    private final SessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;

    public ShipService(JpaShipRepository shipRepository,
                       PlayerRepository playerRepository,
                       ShipMapper shipMapper, CalculateShipSellPriceService sellPriceService,
                       SessionRepository sessionRepository,
                       WebSocketSender webSocketSender) {
        this.shipRepository = shipRepository;
        this.playerRepository = playerRepository;
        this.shipMapper = shipMapper;
        this.sellPriceService = sellPriceService;
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
    }

    public ShipResponse buyShip(BuyShipRequest request) {
        Session session = sessionRepository.findBySessionCode(request.getSessionCode())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionCode()));

        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(request.getPlayerId()));

        if (request.getShipName() == null || request.getShipName().isBlank()) {
            throw new IllegalArgumentException("Ship name is required.");
        }

        ShipType shipType = ShipType.valueOf(request.getShipType().toUpperCase());

        switch (shipType) {
            case CHEAP -> {
                if (session.getCheapShipStock() <= 0) {
                    throw new ShipOutOfStockException(shipType.name());
                }
            }
            case MEDIUM -> {
                if (session.getMediumShipStock() <= 0) {
                    throw new ShipOutOfStockException(shipType.name());
                }
            }
            case EXPENSIVE -> {
                if (session.getExpensiveShipStock() <= 0) {
                    throw new ShipOutOfStockException(shipType.name());
                }
            }
        }

        double price = shipType.getPrice();
        int speed = shipType.getSpeed();

        if (player.getBalance() < price) {
            throw new InsufficientBalanceException(player.getId());
        }

        boolean hasCompanyName = player.getCompanyName() != null && !player.getCompanyName().isBlank();

        if (!hasCompanyName && (request.getCompanyName() == null || request.getCompanyName().isBlank())) {
            throw new IllegalArgumentException("Company name is required.");
        }

        if (!hasCompanyName) {
            player.setCompanyName(request.getCompanyName().trim());
        }

        player.setBalance(player.getBalance() - price);

        Ship ship = new Ship(
                request.getShipName().trim(),
                shipType,
                price,
                speed,
                player
        );

        ship.setCargoCapacity(shipType.getCapacity());
        ship.setCurrentPort(player.getCurrentPort());

        switch (shipType) {
            case CHEAP -> session.setCheapShipStock(session.getCheapShipStock() - 1);
            case MEDIUM -> session.setMediumShipStock(session.getMediumShipStock() - 1);
            case EXPENSIVE -> session.setExpensiveShipStock(session.getExpensiveShipStock() - 1);
        }

        sessionRepository.save(session);
        playerRepository.save(player);
        Ship savedShip = shipRepository.save(ship);

        webSocketSender.sendSessionUpdate(
                session.getSessionCode(),
                new SessionStatusMessage(
                        "STOCK_UPDATED",
                        session.getSessionCode(),
                        session.getStatus().name()
                )
        );

        double sellPrice = sellPriceService.calculate(savedShip);

        return shipMapper.toResponse(savedShip, sellPrice);
    }

    public List<ShipResponse> getShipsByPlayer(Long playerId) {
        return shipRepository.findByOwnerId(playerId)
                .stream()
                .map(ship -> {
                    double sellPrice = sellPriceService.calculate(ship);
                    return shipMapper.toResponse(ship, sellPrice);
                })
                .toList();
    }

    public ShipResponse sellShip(SellShipRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(request.getPlayerId()));

        Ship ship = shipRepository.findById(request.getShipId())
                .orElseThrow(() -> new IllegalArgumentException("Ship not found."));

        if (!ship.getOwner().getId().equals(player.getId())) {
            throw new IllegalArgumentException("This ship does not belong to the player.");
        }

        double sellPrice = sellPriceService.calculate(ship);

        player.setBalance(player.getBalance() + sellPrice);

        shipRepository.delete(ship);
        playerRepository.save(player);

        return shipMapper.toResponse(ship, sellPrice);
    }
}