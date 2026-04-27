package at.fhv.blueroute.cargo.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import org.springframework.stereotype.Service;

@Service
public class CalculateCargoValuesService {

    private final CalculateDeteriorationService deteriorationService;

    public CalculateCargoValuesService(CalculateDeteriorationService deteriorationService) {
        this.deteriorationService = deteriorationService;
    }

    public void apply(Cargo cargo, int distance) {

        // Risk based on distance
        if (distance < 50) {
            cargo.setRiskLevel(RiskLevel.LOW);
        } else if (distance < 120) {
            cargo.setRiskLevel(RiskLevel.MEDIUM);
        } else {
            cargo.setRiskLevel(RiskLevel.HIGH);
        }

        // Price based on distance
        double price = distance * 50;
        cargo.setPrice(price);

        // Reward based on risk
        double reward = price * cargo.getRiskLevel().getFuelMultiplier() + (distance * 10);
        cargo.setReward(reward);

        // Capacity
        cargo.setRequiredCapacity(Math.max(20, distance / 2));

        // Description
        String description = switch (cargo.getRiskLevel()) {
            case LOW -> "Safe delivery";
            case MEDIUM -> "Moderate risk transport";
            case HIGH -> "High-risk shipment";
        };

        description += " from " +
                cargo.getOriginPort().getName() +
                " to " +
                cargo.getDestinationPort().getName();

        cargo.setDescription(description);

        double conditionDamage = deteriorationService.calculate(cargo);
        cargo.setConditionDamage(conditionDamage);
    }
}