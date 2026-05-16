//package at.fhv.blueroute.event.application.service;
//
//import at.fhv.blueroute.event.application.dto.PlannedVoyageEvent;
//import at.fhv.blueroute.event.domain.model.VoyageEventType;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//import java.util.Random;
//
//@Service
//public class VoyageEventPlanningService {
//
//    private final Random random = new Random();
//
//    public Optional<PlannedVoyageEvent> planEventForVoyage(
//            String riskLevel,
//            String cargoType,
//            int currentTick,
//            int arrivalTick
//    ) {
//        int voyageDuration = arrivalTick - currentTick;
//
//        if (voyageDuration <= 1) {
//            return Optional.empty();
//        }
//
//        if (!shouldEventHappen(riskLevel)) {
//            return Optional.empty();
//        }
//
//        VoyageEventType eventType = chooseEventType(cargoType);
//
//        int eventTriggerTick =
//                currentTick + Math.max(1, (int) Math.ceil(voyageDuration / 2.0));
//
//        if (eventTriggerTick >= arrivalTick) {
//            eventTriggerTick = arrivalTick - 1;
//        }
//
//        return Optional.of(
//                new PlannedVoyageEvent(eventType, eventTriggerTick)
//        );
//    }
//
//    private boolean shouldEventHappen(String riskLevel) {
//        int chance = switch (riskLevel) {
//            case "LOW" -> 100;
//            case "MEDIUM" -> 100;
//            case "HIGH" -> 100;
//            default -> 0;
//        };
//
//        return random.nextInt(100) < chance;
//    }
//
//    private VoyageEventType chooseEventType(String cargoType) {
//        return switch (cargoType) {
//            case "OIL" -> VoyageEventType.BURNING_BARRELS;
//            case "LUXURY_GOODS" -> VoyageEventType.PIRATE_DRIP_CHECK;
//            case "FOOD" -> VoyageEventType.RAT_BUFFET;
//            case "ELECTRONICS" -> VoyageEventType.HACKER_SEAGULLS;
//            case "MEDICINE" -> VoyageEventType.MEDICAL_EMERGENCY;
//            default -> VoyageEventType.BAD_WEATHER;
//        };
//    }
//}