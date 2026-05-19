package at.fhv.blueroute.event.presentation.rest;

import at.fhv.blueroute.event.client.EventServiceClient;
import at.fhv.blueroute.event.client.dto.ResolveVoyageEventRequest;
import at.fhv.blueroute.event.client.dto.ResolveVoyageEventResponse;
import at.fhv.blueroute.event.client.dto.VoyageEventDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voyage-events")
public class VoyageEventController {

    private final EventServiceClient eventServiceClient;

    public VoyageEventController(EventServiceClient eventServiceClient) {
        this.eventServiceClient = eventServiceClient;
    }

    @PostMapping("/{voyageId}/resolve")
    public ResolveVoyageEventResponse resolveEvent(
            @PathVariable Long voyageId,
            @Valid @RequestBody ResolveVoyageEventRequest request
    ) {
        return eventServiceClient.resolveEvent(voyageId, request);
    }

    @GetMapping("/{voyageId}/active")
    public ResponseEntity<VoyageEventDto> getActiveEvent(@PathVariable Long voyageId) {
        VoyageEventDto dto = eventServiceClient.getActiveEvent(voyageId);
        if (dto == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(dto);
    }
}