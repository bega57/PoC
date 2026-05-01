package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.common.service.PricingService;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.player.domain.model.Player;
import org.springframework.stereotype.Service;

@Service
public class RepairShipService {

    private final ShipRepository shipRepository;
    private final ShipMapper shipMapper;
    private final PricingService pricingService;
    private final JpaPortRepository portRepository;

    public RepairShipService(ShipRepository shipRepository, ShipMapper shipMapper, PricingService pricingService, JpaPortRepository portRepository) {
        this.shipRepository = shipRepository;
        this.shipMapper = shipMapper;
        this.pricingService = pricingService;
        this.portRepository = portRepository;
    }

    public ShipResponse repair(Long shipId, int repairAmount) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        Port port = portRepository.findByName(ship.getCurrentPort())
                .orElseThrow(() -> new RuntimeException("Port not found"));

        Player player = ship.getOwner();

        double maxCondition = 100.0;
        double currentCondition = Math.max(0, ship.getCondition());

        if (repairAmount <= 0) {
            throw new RuntimeException("Invalid repair amount");
        }

        double missing = maxCondition - currentCondition;

        if (repairAmount > missing) {
            throw new RuntimeException("Repair amount exceeds max condition");
        }

        double basePricePerUnit = 1.5;

        double shipMultiplier = switch (ship.getType()) {
            case CHEAP -> 0.8;
            case MEDIUM -> 1.0;
            case EXPENSIVE -> 1.4;
        };

        double damageFactor = 1 + ((100 - currentCondition) / 100.0);

        double portMultiplier = 0.9 + (port.getFuelPrice() / 15.0);

        double pricePerUnit =
                basePricePerUnit
                        * shipMultiplier
                        * damageFactor
                        * portMultiplier;

        double pricePerUnitGross = pricingService.applyVAT(pricePerUnit);
        double cost = repairAmount * pricePerUnitGross;

        if (player.getBalance() < cost) {
            throw new IllegalArgumentException("Not enough money");
        }

        ship.setCondition(Math.min(100.0, currentCondition + repairAmount));
        player.setBalance(player.getBalance() - cost);

        Ship saved = shipRepository.save(ship);

        return shipMapper.toResponse(saved, 0, cost);
    }

    public double calculateRepairCost(Long shipId, int repairAmount) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        Port port = portRepository.findByName(ship.getCurrentPort())
                .orElseThrow(() -> new RuntimeException("Port not found"));

        double portMultiplier = 0.9 + (port.getFuelPrice() / 15.0);

        double currentCondition = Math.max(0, ship.getCondition());

        double basePricePerUnit = 1.5;

        double shipMultiplier = switch (ship.getType()) {
            case CHEAP -> 0.8;
            case MEDIUM -> 1.0;
            case EXPENSIVE -> 1.4;
        };

        double damageFactor = 1 + ((100 - currentCondition) / 100.0);

        double pricePerUnit = basePricePerUnit * shipMultiplier * damageFactor * portMultiplier;

        double netCost = repairAmount * pricePerUnit;
        return pricingService.applyVAT(netCost);
    }



}