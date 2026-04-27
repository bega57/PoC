package at.fhv.blueroute.cargo.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import org.springframework.stereotype.Service;

@Service
public class CalculateFuelConsumptionService {

    public double calculate(Cargo cargo, double distance) {

        double base = 0.001;

        double capacityFactor = cargo.getRequiredCapacity() / 10.0;

        double riskFactor = 1.0;
        if (cargo.getRiskLevel() != null) {
            riskFactor = cargo.getRiskLevel().getFuelMultiplier();
        }

        double result = distance * base * capacityFactor * riskFactor;
        return Math.round(result * 100.0) / 100.0;
    }
}