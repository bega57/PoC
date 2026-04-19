package at.fhv.blueroute.cargo.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import org.springframework.stereotype.Service;

@Service
public class CalculateDeteriorationService {

    public double calculate(Cargo cargo) {

        double base = cargo.getRequiredTicks();

        double riskFactor = switch (cargo.getRiskLevel()) {
            case LOW -> 0.5;
            case MEDIUM -> 1.0;
            case HIGH -> 2.0;
        };

        return base * riskFactor;
    }
}