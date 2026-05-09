package at.fhv.blueroute.cargo.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.CargoType;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import org.springframework.stereotype.Service;

@Service
public class CalculateCargoValuesService {

    private final CalculateDeteriorationService deteriorationService;

    public CalculateCargoValuesService(CalculateDeteriorationService deteriorationService) {
        this.deteriorationService = deteriorationService;
    }

    public void apply(Cargo cargo, int distance) {

        if (cargo.getType() == null) {
            throw new IllegalArgumentException("Cargo type must not be null.");
        }

        cargo.setRiskLevel(calculateRiskLevel(cargo.getType()));

        double price = calculatePrice(distance, cargo.getType());
        cargo.setPrice(price);

        double reward = calculateReward(price, distance, cargo.getRiskLevel(), cargo.getType());
        cargo.setReward(reward);

        cargo.setRequiredCapacity(calculateRequiredCapacity(distance, cargo.getType()));

        cargo.setDescription(createDescription(cargo));

        double conditionDamage = deteriorationService.calculate(cargo);
        cargo.setConditionDamage(conditionDamage);
    }

    private RiskLevel calculateRiskLevel(CargoType type) {
        return switch (type) {
            case CLOTHES, FOOD -> RiskLevel.LOW;
            case ELECTRONICS, MEDICINE, MACHINERY -> RiskLevel.MEDIUM;
            case OIL, LUXURY_GOODS -> RiskLevel.HIGH;
        };
    }

    private double calculatePrice(int distance, CargoType type) {
        double basePrice = distance * 0.25;

        double typeMultiplier = switch (type) {
            case CLOTHES -> 0.8;
            case FOOD -> 0.9;
            case ELECTRONICS -> 1.0;
            case MEDICINE -> 1.1;
            case MACHINERY -> 1.2;
            case OIL -> 1.3;
            case LUXURY_GOODS -> 1.5;
        };

        return Math.round(basePrice * typeMultiplier);
    }

    private double calculateReward(double price, int distance, RiskLevel riskLevel, CargoType type) {
        double riskBonus = switch (riskLevel) {
            case LOW -> 1.3;
            case MEDIUM -> 1.6;
            case HIGH -> 2.0;
        };

        double typeBonus = switch (type) {
            case CLOTHES -> 150;
            case FOOD -> 200;
            case ELECTRONICS -> 300;
            case MEDICINE -> 350;
            case MACHINERY -> 450;
            case OIL -> 600;
            case LUXURY_GOODS -> 800;
        };

        return Math.round(price * riskBonus + typeBonus);
    }

    private int calculateRequiredCapacity(int distance, CargoType type) {

        int distanceCapacity = Math.max(5, distance / 80);

        int baseCapacity = switch (type) {
            case LUXURY_GOODS -> 10;
            case MEDICINE -> 15;
            case CLOTHES, FOOD, ELECTRONICS -> 20;
            case MACHINERY -> 35;
            case OIL -> 45;
        };

        return Math.max(baseCapacity, distanceCapacity);
    }

    private String createDescription(Cargo cargo) {
        String typeDescription = switch (cargo.getType()) {
            case CLOTHES -> "Clothing shipment";
            case FOOD -> "Food delivery";
            case ELECTRONICS -> "Electronics transport";
            case MEDICINE -> "Medical cargo";
            case MACHINERY -> "Heavy machinery transport";
            case OIL -> "Flammable oil shipment";
            case LUXURY_GOODS -> "Valuable luxury goods shipment";
        };

        return typeDescription + " from " +
                cargo.getOriginPort().getName() +
                " to " +
                cargo.getDestinationPort().getName();
    }
}