package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.voyage.domain.model.VoyageEventType;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class VoyageEventPlanningService {

    private static final int PILOT_STRIKE_CHANCE = 100; // nach Test auf 25 setzen
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

        if (random.nextInt(100) < PILOT_STRIKE_CHANCE) {
            return Optional.of(new PlannedVoyageEvent(VoyageEventType.PILOT_STRIKE, arrivalTick - 1));
        }

        if (!shouldMidVoyageEventHappen(cargo.getRiskLevel().name())) {
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

    private boolean shouldMidVoyageEventHappen(String riskLevel) {
        int chance = switch (riskLevel) {
            case "LOW"    -> 40;
            case "MEDIUM" -> 65;
            case "HIGH"   -> 90;
            default       -> 0;
        };
        return random.nextInt(100) < chance;
    }

    private VoyageEventType chooseEventType(String cargoType) {
        return switch (cargoType) {
            case "OIL"          -> VoyageEventType.BURNING_BARRELS;
            case "LUXURY_GOODS" -> VoyageEventType.PIRATE_DRIP_CHECK;
            case "FOOD"         -> VoyageEventType.RAT_BUFFET;
            case "ELECTRONICS"  -> VoyageEventType.HACKER_SEAGULLS;
            case "MEDICINE"     -> VoyageEventType.MEDICAL_EMERGENCY;
            default             -> VoyageEventType.BAD_WEATHER;
        };
    }

    public record PlannedVoyageEvent(
            VoyageEventType eventType,
            int eventTriggerTick
    ) {}
}