package at.fhv.blueroute.event.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.CargoType;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import at.fhv.blueroute.event.domain.model.VoyageEventType;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class VoyageEventPlanningService {

    private final Random random = new Random();

    public void planEventForVoyage(Voyage voyage, Cargo cargo, int currentTick) {
        if (!shouldEventHappen(cargo.getRiskLevel())) {
            return;
        }

        VoyageEventType eventType = chooseEventType(cargo.getType());

        voyage.setPendingEventType(eventType);
        voyage.setEventTriggerTick(currentTick + 2);
        voyage.setEventTriggered(false);
        voyage.setEventResolved(false);
    }

    private boolean shouldEventHappen(RiskLevel riskLevel) {
        int chance;

        switch (riskLevel) {
            case LOW -> chance = 25;
            case MEDIUM -> chance = 45;
            case HIGH -> chance = 65;
            default -> chance = 25;
        }

        return random.nextInt(100) < chance;
    }

    private VoyageEventType chooseEventType(CargoType cargoType) {
        return switch (cargoType) {
            case OIL -> VoyageEventType.BURNING_BARRELS;
            case LUXURY_GOODS -> VoyageEventType.PIRATE_DRIP_CHECK;
            case FOOD -> VoyageEventType.RAT_BUFFET;
            case ELECTRONICS -> VoyageEventType.HACKER_SEAGULLS;
            case MEDICINE -> VoyageEventType.MEDICAL_EMERGENCY;
            default -> VoyageEventType.BAD_WEATHER;
        };
    }
}