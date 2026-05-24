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
            VoyageResponse voyage = voyageServiceClient.startVoyage(request);
            return ResponseEntity.ok(voyage);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("\"message\"")) {
                int s = msg.indexOf("\"message\":\"") + 11;
                int end = msg.indexOf("\"", s);
                if (s > 10 && end > s) msg = msg.substring(s, end);
            }
            return ResponseEntity
                    .badRequest()
                    .body(java.util.Map.of("message", msg != null ? msg : "Failed to start voyage"));
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

    // ==================== SMUGGLING RESOLVE ====================
    @PostMapping("/{id}/smuggling-resolve")
    public ResponseEntity<VoyageResponse> resolveSmuggling(
            @PathVariable Long id,
            @RequestParam boolean bribe
    ) {
        VoyageResponse result = voyageServiceClient.resolveSmuggling(id, bribe);
        return ResponseEntity.ok(result);
    }
    // ===========================================================
}
