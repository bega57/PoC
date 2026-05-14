package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.voyage.domain.model.VoyageEventType;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class VoyageEventPlanningService {

    private final Random random = new Random();

    public Optional<PlannedVoyageEvent> planEventForVoyage(
            Cargo cargo,
            int currentTick,
            int arrivalTick
    ) {
        int voyageDuration = arrivalTick - currentTick;

        if (voyageDuration <= 1) {
            return Optional.empty();
        }

        if (!shouldEventHappen(cargo.getRiskLevel().name())) {
            return Optional.empty();
        }

        VoyageEventType eventType = chooseEventType(cargo.getType().name());

        int eventTriggerTick =
                currentTick + Math.max(1, (int) Math.ceil(voyageDuration / 2.0));

        if (eventTriggerTick >= arrivalTick) {
            eventTriggerTick = arrivalTick - 1;
        }

        return Optional.of(new PlannedVoyageEvent(eventType, eventTriggerTick));
    }

    private boolean shouldEventHappen(String riskLevel) {
        int chance = switch (riskLevel) {
            case "LOW" -> 100;
            case "MEDIUM" -> 100;
            case "HIGH" -> 100;
            default -> 0;
        };

        return random.nextInt(100) < chance;
    }

    private VoyageEventType chooseEventType(String cargoType) {
        return switch (cargoType) {
            case "OIL" -> VoyageEventType.BURNING_BARRELS;
            case "LUXURY_GOODS" -> VoyageEventType.PIRATE_DRIP_CHECK;
            case "FOOD" -> VoyageEventType.RAT_BUFFET;
            case "ELECTRONICS" -> VoyageEventType.HACKER_SEAGULLS;
            case "MEDICINE" -> VoyageEventType.MEDICAL_EMERGENCY;
            default -> VoyageEventType.BAD_WEATHER;
        };
    }

    public record PlannedVoyageEvent(
            VoyageEventType eventType,
            int eventTriggerTick
    ) {
    }
}