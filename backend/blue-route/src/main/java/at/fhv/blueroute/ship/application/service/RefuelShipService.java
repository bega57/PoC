package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.common.service.PricingService;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import at.fhv.blueroute.player.client.PlayerServiceClient;

import org.springframework.stereotype.Service;

@Service
public class RefuelShipService {

    private final ShipRepository shipRepository;
    private final ShipMapper shipMapper;
    private final JpaPortRepository portRepository;
    private final PricingService pricingService;
    private final PlayerServiceClient playerServiceClient;

    public RefuelShipService(ShipRepository shipRepository,
                             ShipMapper shipMapper,
                             JpaPortRepository portRepository,
                             PricingService pricingService,
                             PlayerServiceClient playerServiceClient) {
        this.shipRepository = shipRepository;
        this.shipMapper = shipMapper;
        this.portRepository = portRepository;
        this.pricingService = pricingService;
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

        Port port = portRepository.findByName(ship.getCurrentPort())
                .orElseThrow(() -> new RuntimeException("Port not found"));

        double basePrice = port.getFuelPrice();

        double multiplier;

        switch (ship.getType()) {
            case CHEAP -> multiplier = 1.0;
            case MEDIUM -> multiplier = 1.1;
            case EXPENSIVE -> multiplier = 1.25;
            default -> multiplier = 1.0;
        }

        double netPrice = basePrice * multiplier * 0.6;
        double pricePerUnit = pricingService.applyVAT(netPrice);

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

        Port port = portRepository.findByName(ship.getCurrentPort())
                .orElseThrow(() -> new RuntimeException("Port not found"));

        double basePrice = port.getFuelPrice();

        double multiplier = switch (ship.getType()) {
            case CHEAP -> 1.0;
            case MEDIUM -> 1.2;
            case EXPENSIVE -> 1.4;
        };

        double netPrice = basePrice * multiplier * 0.6;
        double pricePerUnit = pricingService.applyVAT(netPrice);

        return fuelAmount * pricePerUnit;
    }
}