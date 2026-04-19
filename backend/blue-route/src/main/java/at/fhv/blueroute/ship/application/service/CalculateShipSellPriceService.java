package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.ship.domain.model.Ship;
import org.springframework.stereotype.Service;

@Service
public class CalculateShipSellPriceService {

    public double calculate(Ship ship) {

        double base = ship.getType().getPrice();

        double depreciation = 0.85;

        double conditionFactor = ship.getCondition() / 100.0;

        double fuelFactor = 0.9 + (ship.getFuelLevel() / 100.0) * 0.1;

        return Math.floor(base * depreciation * conditionFactor * fuelFactor);
    }
}