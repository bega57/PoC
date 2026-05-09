package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import at.fhv.blueroute.ship.player.client.PlayerServiceClient;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import org.springframework.stereotype.Service;

@Service
public class RefuelShipService {

    private final ShipRepository shipRepository;
    private final ShipMapper shipMapper;
    private final PlayerServiceClient playerServiceClient;

    public RefuelShipService(
            ShipRepository shipRepository,
            ShipMapper shipMapper,
            PlayerServiceClient playerServiceClient
    ) {
        this.shipRepository = shipRepository;
        this.shipMapper = shipMapper;
        this.playerServiceClient = playerServiceClient;
    }

    public ShipResponse refuel(Long shipId, int requestedFuel) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        int maxFuel = 100;
        int currentFuel = ship.getFuelLevel();

        if (requestedFuel <= 0) {
            throw new RuntimeException("Invalid fuel amount");
        }

        double basePrice = 5.0;

        double multiplier;

        switch (ship.getType()) {
            case CHEAP -> multiplier = 1.0;
            case MEDIUM -> multiplier = 1.1;
            case EXPENSIVE -> multiplier = 1.25;
            default -> multiplier = 1.0;
        }

        double netPrice = basePrice * multiplier * 0.6;
        double pricePerUnit = netPrice * 1.2;

        int maxByTank = (int) Math.floor(maxFuel - currentFuel);

        if (requestedFuel > maxByTank) {
            throw new IllegalArgumentException("Fuel exceeds tank capacity");
        }

        double cost = requestedFuel * pricePerUnit;

        ship.setFuelLevel(Math.min(100, currentFuel + requestedFuel));

        playerServiceClient.updateBalance(
                ship.getOwnerId(),
                -cost,
                "REFUEL"
        );

        Ship saved = shipRepository.save(ship);

        return shipMapper.toResponse(saved, 0, cost);
    }

    public double calculateCost(Long shipId, int fuelAmount) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        double basePrice = 5.0;

        double multiplier = switch (ship.getType()) {
            case CHEAP -> 1.0;
            case MEDIUM -> 1.2;
            case EXPENSIVE -> 1.4;
        };

        double netPrice = basePrice * multiplier * 0.6;
        double pricePerUnit = netPrice * 1.2;

        return fuelAmount * pricePerUnit;
    }
}