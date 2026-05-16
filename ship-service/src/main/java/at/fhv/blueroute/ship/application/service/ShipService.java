package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.ship.application.exception.PlayerNotFoundException;
import at.fhv.blueroute.ship.player.client.PlayerServiceClient;
import at.fhv.blueroute.ship.player.client.dto.PlayerResponse;
import at.fhv.blueroute.ship.application.exception.ShipCurrentlyTravelingException;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.model.ShipType;
import at.fhv.blueroute.ship.domain.model.UsedShipOffer;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaUsedShipOfferRepository;
import at.fhv.blueroute.ship.presentation.dto.BuyShipRequest;
import at.fhv.blueroute.ship.presentation.dto.BuyUsedShipRequest;
import at.fhv.blueroute.ship.presentation.dto.SellShipRequest;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShipService {

    private final JpaShipRepository shipRepository;
    private final JpaUsedShipOfferRepository usedShipOfferRepository;
    private final ShipMapper shipMapper;
    private final CalculateShipSellPriceService sellPriceService;
    private final PlayerServiceClient playerServiceClient;

    public ShipService(
            JpaShipRepository shipRepository,
            JpaUsedShipOfferRepository usedShipOfferRepository,
            ShipMapper shipMapper,
            CalculateShipSellPriceService sellPriceService,
            PlayerServiceClient playerServiceClient
    ) {
        this.shipRepository = shipRepository;
        this.usedShipOfferRepository = usedShipOfferRepository;
        this.shipMapper = shipMapper;
        this.sellPriceService = sellPriceService;
        this.playerServiceClient = playerServiceClient;
    }

    public ShipResponse buyShip(BuyShipRequest request) {

        PlayerResponse player =
                playerServiceClient.getPlayer(request.getPlayerId());

        if (player == null) {
            throw new PlayerNotFoundException(
                    request.getPlayerId()
            );
        }

        if (request.getShipName() == null
                || request.getShipName().isBlank()) {
            throw new IllegalArgumentException("Ship name required");
        }

        ShipType shipType =
                ShipType.valueOf(request.getShipType().toUpperCase());

        double finalPrice = shipType.getPrice() * 1.2;

        playerServiceClient.updateBalance(
                player.getId(),
                -finalPrice,
                "SHIP_PURCHASE"
        );

        Ship ship = new Ship(
                request.getShipName().trim(),
                shipType,
                finalPrice,
                shipType.getSpeed(),
                player.getId()
        );

        ship.setCargoCapacity(shipType.getCapacity());
        ship.setCurrentPort(player.getCurrentPort());

        Ship savedShip = shipRepository.save(ship);

        double sellPrice = sellPriceService.calculate(savedShip);

        return shipMapper.toResponse(savedShip, sellPrice, 0);
    }

    public List<ShipResponse> getShipsByPlayer(Long playerId) {

        return shipRepository.findByOwnerId(playerId)
                .stream()
                .map(ship -> {
                    double sellPrice =
                            sellPriceService.calculate(ship);

                    return shipMapper.toResponse(
                            ship,
                            sellPrice,
                            0
                    );
                })
                .toList();
    }

    public ShipResponse sellShip(SellShipRequest request) {

        Ship ship = shipRepository.findById(request.getShipId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Ship not found"));

        if (!ship.getOwnerId().equals(request.getPlayerId())) {
            throw new IllegalArgumentException("Not your ship");
        }

        if (ship.isTraveling()) {
            throw new ShipCurrentlyTravelingException(ship.getName());
        }

        double sellPrice = sellPriceService.calculate(ship);

        double usedMarketPrice =
                Math.floor(sellPrice * 1.1);

        UsedShipOffer usedShipOffer =
                new UsedShipOffer(
                        ship.getType(),
                        usedMarketPrice,
                        ship.getCondition(),
                        ship.getFuelLevel()
                );

        usedShipOfferRepository.save(
                usedShipOffer
        );

        playerServiceClient.updateBalance(
                request.getPlayerId(),
                sellPrice,
                "SHIP_SALE"
        );

        shipRepository.delete(ship);

        return shipMapper.toResponse(ship, sellPrice, 0);
    }

    public ShipResponse buyUsedShip(
            Long offerId,
            BuyUsedShipRequest request
    ) {

        PlayerResponse player =
                playerServiceClient.getPlayer(request.getPlayerId());

        if (player == null) {
            throw new PlayerNotFoundException(
                    request.getPlayerId()
            );
        }

        UsedShipOffer offer =
                usedShipOfferRepository.findById(offerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Offer not found"
                                ));

        double finalPrice = offer.getPrice() * 1.2;

        playerServiceClient.updateBalance(
                player.getId(),
                -finalPrice,
                "USED_SHIP_PURCHASE"
        );

        Ship ship = new Ship(
                request.getShipName().trim(),
                offer.getType(),
                finalPrice,
                offer.getType().getSpeed(),
                player.getId()
        );

        ship.setCargoCapacity(offer.getType().getCapacity());
        ship.setCondition((int) offer.getCondition());
        ship.setFuelLevel((int) offer.getFuelLevel());
        ship.setCurrentPort(player.getCurrentPort());

        Ship savedShip = shipRepository.save(ship);

        usedShipOfferRepository.delete(offer);

        double sellPrice = sellPriceService.calculate(savedShip);

        return shipMapper.toResponse(savedShip, sellPrice, 0);
    }

    public ShipResponse getShip(Long shipId) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Ship not found"));

        double sellPrice =
                sellPriceService.calculate(ship);

        return shipMapper.toResponse(
                ship,
                sellPrice,
                0
        );
    }
    public void startVoyage(
            Long shipId,
            double usedCapacity
    ) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Ship not found"));

        ship.setTraveling(true);
        ship.setCurrentPort(null);

        ship.setUsedCapacity(
                ship.getUsedCapacity() + usedCapacity
        );

        shipRepository.save(ship);
    }

    public void finishVoyage(
            Long shipId,
            String destinationPort,
            double releasedCapacity
    ) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Ship not found"));

        ship.setTraveling(false);
        ship.setCurrentPort(destinationPort);

        ship.setUsedCapacity(
                Math.max(
                        0,
                        ship.getUsedCapacity() - releasedCapacity
                )
        );

        shipRepository.save(ship);
    }

    @Transactional
    public void updateVoyageProgress(
            Long shipId,
            double fuelLoss,
            double conditionLoss
    ) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() ->
                        new RuntimeException("Ship not found"));

        int newFuel =
                (int) Math.max(
                        0,
                        Math.floor(ship.getFuelLevel() - fuelLoss)
                );

        int newCondition =
                (int) Math.max(
                        0,
                        Math.floor(ship.getCondition() - conditionLoss)
                );

        ship.setFuelLevel(newFuel);
        ship.setCondition(newCondition);

        shipRepository.save(ship);
    }

    public List<ShipResponse> getAllShips() {

        return shipRepository.findAll()
                .stream()
                .map(ship -> {
                    double sellPrice =
                            sellPriceService.calculate(ship);

                    return shipMapper.toResponse(
                            ship,
                            sellPrice,
                            0
                    );
                })
                .toList();
    }
}