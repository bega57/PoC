package at.fhv.blueroute.event.presentation.rest;

import at.fhv.blueroute.event.application.service.VoyageEventResolveService;
import at.fhv.blueroute.event.application.service.VoyageEventTriggerService;
import at.fhv.blueroute.event.presentation.dto.ResolveVoyageEventRequest;
import at.fhv.blueroute.event.presentation.dto.ResolveVoyageEventResponse;
import at.fhv.blueroute.event.presentation.dto.VoyageEventDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voyage-events")
public class VoyageEventController {

    private final VoyageEventTriggerService triggerService;
    private final VoyageEventResolveService resolveService;

    public VoyageEventController(VoyageEventTriggerService triggerService,
                                 VoyageEventResolveService resolveService) {
        this.triggerService = triggerService;
        this.resolveService = resolveService;
    }

    @PostMapping("/process-tick")
    public ResponseEntity<Boolean> processTick(
            @RequestParam Long sessionId,
            @RequestParam String sessionCode,
            @RequestParam int currentTick
    ) {
        boolean anyTriggered = triggerService.processTickForSession(
                sessionId, sessionCode, currentTick
        );
        return ResponseEntity.ok(anyTriggered);
    }

    @PostMapping("/{voyageId}/resolve")
    public ResolveVoyageEventResponse resolveEvent(
            @PathVariable Long voyageId,
            @Valid @RequestBody ResolveVoyageEventRequest request
    ) {
        String message = resolveService.resolveEvent(
                voyageId,
                request.getSelectedOption()
        );
        return new ResolveVoyageEventResponse(message);
    }

    @GetMapping("/health")
    public String health() {
        return "event-service running";
    }

    @GetMapping("/{voyageId}/active")
    public ResponseEntity<VoyageEventDto> getActiveEvent(@PathVariable Long voyageId) {
        VoyageEventDto dto = triggerService.getActiveEventForVoyage(voyageId);
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }
}