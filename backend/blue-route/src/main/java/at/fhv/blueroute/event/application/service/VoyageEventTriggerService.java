package at.fhv.blueroute.event.application.service;

import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.event.domain.model.VoyageEventType;
import at.fhv.blueroute.event.presentation.dto.VoyageEventDto;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoyageEventTriggerService {

    private final JpaVoyageRepository voyageRepository;
    private final WebSocketSender webSocketSender;

    public VoyageEventTriggerService(JpaVoyageRepository voyageRepository,
                                     WebSocketSender webSocketSender) {
        this.voyageRepository = voyageRepository;
        this.webSocketSender = webSocketSender;
    }

    public void triggerEventIfNeeded(Voyage voyage, Session session) {
        if (voyage.getPendingEventType() == null) {
            return;
        }

        if (voyage.isEventTriggered()) {
            return;
        }

        if (voyage.isEventResolved()) {
            return;
        }

        if (voyage.getEventTriggerTick() == null) {
            throw new IllegalStateException(
                    "Voyage has pending event but no trigger tick. Voyage id: " + voyage.getId()
            );
        }

        if (session.getCurrentTick() < voyage.getEventTriggerTick()) {
            return;
        }

        voyage.setEventTriggered(true);
        voyageRepository.save(voyage);

        VoyageEventDto dto = createDto(voyage);

        webSocketSender.sendSessionUpdate(session.getSessionCode(), dto);
    }

    private VoyageEventDto createDto(Voyage voyage) {
        VoyageEventType type = voyage.getPendingEventType();

        return switch (type) {
            case BURNING_BARRELS -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Burning Barrels",
                    "A sailor smoked next to the oil barrels. Now it smells like BBQ and panic.",
                    List.of("Extinguish the fire", "Keep sailing", "Throw barrels overboard")
            );

            case PIRATE_DRIP_CHECK -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Pirate Drip Check",
                    "Pirates spotted your luxury goods and now want to look fresh too.",
                    List.of("Bribe the pirates", "Escape", "Hide the cargo")
            );

            case RAT_BUFFET -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Rat Buffet",
                    "Rats opened an all-you-can-eat buffet inside your cargo hold.",
                    List.of("Buy rat traps", "Ignore them")
            );

            case HACKER_SEAGULLS -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Hacker Seagulls",
                    "Seagulls with USB sticks are sabotaging your ship electronics.",
                    List.of("Restart the system", "Call a technician", "Ignore it")
            );

            case MEDICAL_EMERGENCY -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Medical Emergency",
                    "The crew used the medicine cargo against seasickness. Very professional.",
                    List.of("Order replacement medicine", "Accept the loss")
            );

            case BAD_WEATHER -> new VoyageEventDto(
                    voyage.getId(),
                    type,
                    "Bad Weather",
                    "The ocean has entered boss fight mode. The waves are taller than your motivation.",
                    List.of("Sail around the storm", "Sail through it")
            );
        };
    }
}