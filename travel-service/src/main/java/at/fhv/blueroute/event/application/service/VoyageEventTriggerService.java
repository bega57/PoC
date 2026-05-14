package at.fhv.blueroute.event.application.service;

import at.fhv.blueroute.event.domain.model.VoyageEventType;
import at.fhv.blueroute.event.presentation.dto.VoyageEventDto;
import at.fhv.blueroute.event.presentation.dto.VoyageEventOptionDto;
import at.fhv.blueroute.travel.domain.model.Voyage;
import at.fhv.blueroute.travel.infrastructure.persistence.JpaVoyageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoyageEventTriggerService {

    private final JpaVoyageRepository voyageRepository;

    public VoyageEventTriggerService(JpaVoyageRepository voyageRepository) {
        this.voyageRepository = voyageRepository;
    }

    public VoyageEventDto triggerEventIfNeeded(Voyage voyage, int currentTick) {

        if (voyage.getPendingEventType() == null) return null;
        if (voyage.isEventTriggered()) return null;
        if (voyage.isEventResolved()) return null;

        if (voyage.getEventTriggerTick() == null) {
            throw new IllegalStateException(
                    "Voyage has pending event but no trigger tick. Voyage id: " + voyage.getId()
            );
        }

        if (currentTick < voyage.getEventTriggerTick()) return null;

        voyage.setEventTriggered(true);
        voyageRepository.save(voyage);

        return createDto(voyage);
    }

    private VoyageEventDto createDto(Voyage voyage) {
        VoyageEventType type = voyage.getPendingEventType();

        return switch (type) {
            case BURNING_BARRELS -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Burning Barrels",
                    "A sailor smoked next to the oil barrels. Now it smells like BBQ and panic.",
                    List.of(
                            new VoyageEventOptionDto("Extinguish the fire", "-8 ship condition, +1 day, extra fuel usage"),
                            new VoyageEventOptionDto("Keep sailing", "-18 ship condition"),
                            new VoyageEventOptionDto("Throw barrels overboard", "-25% reward")
                    )
            );

            case PIRATE_DRIP_CHECK -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Pirate Drip Check",
                    "Pirates spotted your luxury goods and now want to look fresh too.",
                    List.of(
                            new VoyageEventOptionDto("Bribe the pirates", "-500 coins"),
                            new VoyageEventOptionDto("Escape", "+2 days, extra fuel usage and condition loss"),
                            new VoyageEventOptionDto("Hide the cargo", "-15% reward")
                    )
            );

            case RAT_BUFFET -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Rat Buffet",
                    "Rats opened an all-you-can-eat buffet inside your cargo hold.",
                    List.of(
                            new VoyageEventOptionDto("Buy rat traps", "-200 coins"),
                            new VoyageEventOptionDto("Ignore them", "-20% reward")
                    )
            );

            case HACKER_SEAGULLS -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Hacker Seagulls",
                    "Seagulls with USB sticks are sabotaging your ship electronics.",
                    List.of(
                            new VoyageEventOptionDto("Restart the system", "+1 day, extra fuel usage and condition loss"),
                            new VoyageEventOptionDto("Call a technician", "-350 coins"),
                            new VoyageEventOptionDto("Ignore it", "-12 ship condition")
                    )
            );

            case MEDICAL_EMERGENCY -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Medical Emergency",
                    "The crew used the medicine cargo against seasickness. Very professional.",
                    List.of(
                            new VoyageEventOptionDto("Order replacement medicine", "-300 coins"),
                            new VoyageEventOptionDto("Accept the loss", "-18% reward")
                    )
            );

            case BAD_WEATHER -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Bad Weather",
                    "The ocean has entered boss fight mode. The waves are taller than your motivation.",
                    List.of(
                            new VoyageEventOptionDto("Sail around the storm", "+2 days, extra fuel usage and condition loss"),
                            new VoyageEventOptionDto("Sail through it", "-15 ship condition")
                    )
            );
        };
    }
}