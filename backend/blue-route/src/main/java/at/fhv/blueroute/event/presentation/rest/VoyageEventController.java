package at.fhv.blueroute.event.presentation.rest;

import at.fhv.blueroute.event.application.service.VoyageEventResolveService;
import at.fhv.blueroute.event.presentation.dto.ResolveVoyageEventRequest;
import at.fhv.blueroute.event.presentation.dto.ResolveVoyageEventResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voyage-events")
public class VoyageEventController {

    private final VoyageEventResolveService voyageEventResolveService;

    public VoyageEventController(VoyageEventResolveService voyageEventResolveService) {
        this.voyageEventResolveService = voyageEventResolveService;
    }

    @PostMapping("/{voyageId}/resolve")
    public ResolveVoyageEventResponse resolveEvent(
            @PathVariable Long voyageId,
            @Valid @RequestBody ResolveVoyageEventRequest request
    ) {
        String message = voyageEventResolveService.resolveEvent(
                voyageId,
                request.getSelectedOption()
        );

        return new ResolveVoyageEventResponse(message);
    }
}