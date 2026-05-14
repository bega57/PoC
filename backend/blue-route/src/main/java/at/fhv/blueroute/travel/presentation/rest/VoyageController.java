package at.fhv.blueroute.travel.presentation.rest;

import at.fhv.blueroute.travel.client.TravelServiceClient;
import at.fhv.blueroute.travel.client.dto.StartVoyageRequest;
import at.fhv.blueroute.travel.client.dto.VoyageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voyages")
public class VoyageController {

    private final TravelServiceClient travelServiceClient;

    public VoyageController(TravelServiceClient travelServiceClient) {
        this.travelServiceClient = travelServiceClient;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startVoyage(@RequestBody StartVoyageRequest request) {

        try {
            VoyageResponse voyage =
                    travelServiceClient.startVoyage(request);

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

        return travelServiceClient.getVoyages(
                sessionId,
                currentTick
        );
    }
}