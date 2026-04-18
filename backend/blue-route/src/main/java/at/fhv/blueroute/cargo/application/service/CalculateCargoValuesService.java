package at.fhv.blueroute.cargo.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import org.springframework.stereotype.Service;

    @Service
    public class CalculateCargoValuesService {

        public void apply(Cargo cargo, int distance) {

            // risk based on distance
            if (distance < 50) {
                cargo.setRiskLevel(RiskLevel.LOW);
            } else if (distance < 120) {
                cargo.setRiskLevel(RiskLevel.MEDIUM);
            } else {
                cargo.setRiskLevel(RiskLevel.HIGH);
            }

            // price based on distance
            double price = distance * 50;
            cargo.setPrice(price);

            // reward based on risk
            double reward = price * cargo.getRiskLevel().getFuelMultiplier() + (distance * 10);
            cargo.setReward(reward);

            // capacity
            cargo.setRequiredCapacity(Math.max(20, distance / 2));
        }
    }
