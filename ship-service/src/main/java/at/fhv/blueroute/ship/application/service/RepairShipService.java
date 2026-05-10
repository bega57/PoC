package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.ship.application.exception.InvalidRepairAmountException;
import at.fhv.blueroute.ship.application.exception.RepairLimitExceededException;
import at.fhv.blueroute.ship.application.exception.ShipNotFoundException;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import at.fhv.blueroute.ship.player.client.PlayerServiceClient;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import org.springframework.stereotype.Service;

@Service
public class RepairShipService {

    private final ShipRepository shipRepository;
    private final ShipMapper shipMapper;
    private final PlayerServiceClient playerServiceClient;

    public RepairShipService(
            ShipRepository shipRepository,
            ShipMapper shipMapper,
            PlayerServiceClient playerServiceClient
    ) {
        this.shipRepository = shipRepository;
        this.shipMapper = shipMapper;
        this.playerServiceClient = playerServiceClient;
    }

    public ShipResponse repair(Long shipId, int repairAmount) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() ->
                        new ShipNotFoundException(shipId));

        int maxCondition = 100;
        int currentCondition = Math.max(0, ship.getCondition());

        if (repairAmount <= 0) {
            throw new InvalidRepairAmountException();
        }

        double missing = maxCondition - currentCondition;

        if (repairAmount > missing) {
            throw new RepairLimitExceededException();
        }

        double basePricePerUnit = 1.5;

        double shipMultiplier = switch (ship.getType()) {
            case CHEAP -> 0.8;
            case MEDIUM -> 1.0;
            case EXPENSIVE -> 1.4;
        };

        double damageFactor = 1 + ((100 - currentCondition) / 100.0);

        double portMultiplier = 1.0;

        double pricePerUnit =
                basePricePerUnit
                        * shipMultiplier
                        * damageFactor
                        * portMultiplier;

        double pricePerUnitGross = pricePerUnit * 1.2;
        double cost = repairAmount * pricePerUnitGross;

        ship.setCondition(Math.min(100, currentCondition + repairAmount));

        playerServiceClient.updateBalance(
                ship.getOwnerId(),
                -cost,
                "SHIP_REPAIR"
        );

        Ship saved = shipRepository.save(ship);

        return shipMapper.toResponse(saved, 0, cost);
    }

    public double calculateRepairCost(Long shipId, int repairAmount) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() ->
                        new ShipNotFoundException(shipId));

        int currentCondition = Math.max(0, ship.getCondition());

        double basePricePerUnit = 1.5;

        double shipMultiplier = switch (ship.getType()) {
            case CHEAP -> 0.8;
            case MEDIUM -> 1.0;
            case EXPENSIVE -> 1.4;
        };

        double damageFactor = 1 + ((100 - currentCondition) / 100.0);

        double portMultiplier = 1.0;

        double pricePerUnit =
                basePricePerUnit
                        * shipMultiplier
                        * damageFactor
                        * portMultiplier;

        double netCost = repairAmount * pricePerUnit;

        return netCost * 1.2;
    }
}