package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.ship.domain.model.Ship;
import org.springframework.stereotype.Service;

@Service
public class CalculateShipSellPriceService {

    public double calculate(Ship ship) {

        double base = ship.getType().getPrice();

        double minFactor = 0.1;

        double conditionFactor = Math.max(minFactor, ship.getCondition() / 100.0);
        double fuelFactor = Math.max(minFactor, ship.getFuelLevel() / 100.0);

        double sellPenalty = 0.9;

        return Math.floor(base * conditionFactor * fuelFactor * sellPenalty);
    }
}