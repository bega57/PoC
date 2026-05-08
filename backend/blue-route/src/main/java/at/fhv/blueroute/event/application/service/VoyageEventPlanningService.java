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
        int voyageDuration = voyage.getArrivalTick() - currentTick;

        if (voyageDuration <= 1) {
            return;
        }

        if (!shouldEventHappen(cargo.getRiskLevel())) {
            return;
        }

        VoyageEventType eventType = chooseEventType(cargo.getType());

        int eventTriggerTick = currentTick + Math.max(1, (int) Math.ceil(voyageDuration / 2.0));

        if (eventTriggerTick >= voyage.getArrivalTick()) {
            eventTriggerTick = voyage.getArrivalTick() - 1;
        }

        voyage.setPendingEventType(eventType);
        voyage.setEventTriggerTick(eventTriggerTick);
        voyage.setEventTriggered(false);
        voyage.setEventResolved(false);
    }

    private boolean shouldEventHappen(RiskLevel riskLevel) {
        int chance;

        switch (riskLevel) {
            case LOW -> chance = 100;
            case MEDIUM -> chance = 100;
            case HIGH -> chance = 100;
            default -> chance = 100;
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