package at.fhv.blueroute.voyage.presentation.rest;

import at.fhv.blueroute.voyage.client.VoyageServiceClient;
import at.fhv.blueroute.voyage.client.dto.StartVoyageRequest;
import at.fhv.blueroute.voyage.client.dto.VoyageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voyages")
public class VoyageController {

    private final VoyageServiceClient voyageServiceClient;

    public VoyageController(VoyageServiceClient voyageServiceClient) {
        this.voyageServiceClient = voyageServiceClient;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startVoyage(@RequestBody StartVoyageRequest request) {

        try {
            VoyageResponse voyage =
                    voyageServiceClient.startVoyage(request);

            return ResponseEntity.ok(voyage);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<VoyageResponse> getVoyages(
            @RequestParam Long sessionId,
            @RequestParam int currentTick
    ) {

        return voyageServiceClient.getVoyages(
                sessionId,
                currentTick
        );
    }
}