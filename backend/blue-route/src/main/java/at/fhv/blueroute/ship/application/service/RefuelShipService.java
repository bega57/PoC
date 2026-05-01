package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.common.service.PricingService;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import org.springframework.stereotype.Service;

@Service
public class RefuelShipService {

    private final ShipRepository shipRepository;
    private final ShipMapper shipMapper;
    private final JpaPortRepository portRepository;
    private final PricingService pricingService;

    public RefuelShipService(ShipRepository shipRepository, ShipMapper shipMapper, JpaPortRepository portRepository, PricingService pricingService) {
        this.shipRepository = shipRepository;
        this.shipMapper = shipMapper;
        this.portRepository = portRepository;
        this.pricingService = pricingService;
    }

    public ShipResponse refuel(Long shipId, int requestedFuel) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        Player player = ship.getOwner();

        double maxFuel = 100.0;
        double currentFuel = ship.getFuelLevel();

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

        double maxByTank = maxFuel - currentFuel;

        if (requestedFuel > maxByTank) {
            throw new RuntimeException("Fuel exceeds tank capacity");
        }

        double cost = requestedFuel * pricePerUnit;

        if (player.getBalance() < cost) {
            throw new IllegalArgumentException("Not enough money");
        }


        ship.setFuelLevel(Math.min(100.0, currentFuel + requestedFuel));
        player.setBalance(player.getBalance() - cost);

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