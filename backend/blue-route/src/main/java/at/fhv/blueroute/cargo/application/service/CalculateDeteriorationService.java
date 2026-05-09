package at.fhv.blueroute.cargo.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import org.springframework.stereotype.Service;

@Service
public class CalculateDeteriorationService {

    public double calculate(Cargo cargo) {

        double base = cargo.getRequiredTicks() * 0.08;

        double riskFactor = switch (cargo.getRiskLevel()) {
            case LOW -> 0.8;
            case MEDIUM -> 1.2;
            case HIGH -> 1.6;
        };

        return Math.round(base * riskFactor * 100.0) / 100.0;
    }
}