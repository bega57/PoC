package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.common.websocket.SessionStatusMessage;
import at.fhv.blueroute.player.application.exception.PlayerNotFoundException;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import at.fhv.blueroute.ship.application.exception.InsufficientBalanceException;
import at.fhv.blueroute.ship.application.exception.ShipCurrentlyTravelingException;
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
import at.fhv.blueroute.ship.domain.model.UsedShipOffer;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaUsedShipOfferRepository;
import at.fhv.blueroute.ship.presentation.dto.BuyUsedShipRequest;
import at.fhv.blueroute.common.service.PricingService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipService {

    private final JpaShipRepository shipRepository;
    private final JpaUsedShipOfferRepository usedShipOfferRepository;
    private final PlayerRepository playerRepository;
    private final ShipMapper shipMapper;
    private final CalculateShipSellPriceService sellPriceService;
    private final SessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;
    private final PricingService pricingService;

    public ShipService(JpaShipRepository shipRepository,
                       JpaUsedShipOfferRepository usedShipOfferRepository,
                       PlayerRepository playerRepository,
                       ShipMapper shipMapper, CalculateShipSellPriceService sellPriceService,
                       SessionRepository sessionRepository,
                       WebSocketSender webSocketSender, PricingService pricingService) {
        this.shipRepository = shipRepository;
        this.usedShipOfferRepository = usedShipOfferRepository;
        this.playerRepository = playerRepository;
        this.shipMapper = shipMapper;
        this.sellPriceService = sellPriceService;
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
        this.pricingService = pricingService;
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

        double basePrice = shipType.getPrice();
        double finalPrice = pricingService.applyVAT(basePrice);
        int speed = shipType.getSpeed();

        if (player.getBalance() < finalPrice) {
            throw new InsufficientBalanceException(player.getId());
        }

        boolean hasCompanyName = player.getCompanyName() != null && !player.getCompanyName().isBlank();

        if (!hasCompanyName && (request.getCompanyName() == null || request.getCompanyName().isBlank())) {
            throw new IllegalArgumentException("Company name is required.");
        }

        if (!hasCompanyName) {
            player.setCompanyName(request.getCompanyName().trim());
        }

        player.setBalance(player.getBalance() - finalPrice);

        Ship ship = new Ship(
                request.getShipName().trim(),
                shipType,
                finalPrice,
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

        return shipMapper.toResponse(savedShip, sellPrice, 0);
    }

    public List<ShipResponse> getShipsByPlayer(Long playerId) {
        return shipRepository.findByOwnerId(playerId)
                .stream()
                .map(ship -> {
                    double sellPrice = sellPriceService.calculate(ship);
                    return shipMapper.toResponse(ship, sellPrice, 0);
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

        if (ship.isTraveling()) {
            throw new ShipCurrentlyTravelingException(ship.getName());
        }

        double sellPrice = sellPriceService.calculate(ship);

        Session session = sessionRepository.findBySessionCode(request.getSessionCode())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionCode()));

        double usedMarketPrice = Math.floor(sellPrice * 1.1);

        UsedShipOffer usedShipOffer = new UsedShipOffer(
                ship.getType(),
                usedMarketPrice,
                ship.getCondition(),
                ship.getFuelLevel(),
                session
        );

        usedShipOfferRepository.save(usedShipOffer);

        player.setBalance(player.getBalance() + sellPrice);

        shipRepository.delete(ship);
        playerRepository.save(player);

        return shipMapper.toResponse(ship, sellPrice, 0);
    }

    public ShipResponse buyUsedShip(Long offerId, BuyUsedShipRequest request) {
        Session session = sessionRepository.findBySessionCode(request.getSessionCode())
                .orElseThrow(() -> new SessionNotFoundException(request.getSessionCode()));

        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(request.getPlayerId()));

        UsedShipOffer offer = usedShipOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Used ship offer not found."));

        if (!offer.getSession().getSessionCode().equals(session.getSessionCode())) {
            throw new IllegalArgumentException("Used ship offer does not belong to this session.");
        }

        if (request.getShipName() == null || request.getShipName().isBlank()) {
            throw new IllegalArgumentException("Ship name is required.");
        }

        double finalPrice = pricingService.applyVAT(offer.getPrice());

        if (player.getBalance() < finalPrice) {
            throw new InsufficientBalanceException(player.getId());
        }

        player.setBalance(player.getBalance() - finalPrice);

        Ship ship = new Ship(
                request.getShipName().trim(),
                offer.getType(),
                finalPrice,
                offer.getType().getSpeed(),
                player
        );

        ship.setCargoCapacity(offer.getType().getCapacity());
        ship.setCondition(offer.getCondition());
        ship.setFuelLevel(offer.getFuelLevel());
        ship.setCurrentPort(player.getCurrentPort());

        playerRepository.save(player);
        Ship savedShip = shipRepository.save(ship);
        usedShipOfferRepository.delete(offer);

        double sellPrice = sellPriceService.calculate(savedShip);
        return shipMapper.toResponse(savedShip, sellPrice, 0);
    }
}