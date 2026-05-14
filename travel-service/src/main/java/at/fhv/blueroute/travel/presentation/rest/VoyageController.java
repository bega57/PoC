package at.fhv.blueroute.travel.presentation.rest;

import at.fhv.blueroute.travel.application.service.FinishVoyageService;
import at.fhv.blueroute.travel.application.service.GetVoyagesService;
import at.fhv.blueroute.travel.application.service.ProcessVoyageTickService;
import at.fhv.blueroute.travel.application.service.StartVoyageService;
import at.fhv.blueroute.travel.domain.model.Voyage;
import at.fhv.blueroute.travel.infrastructure.persistence.JpaVoyageRepository;
import at.fhv.blueroute.travel.presentation.dto.StartVoyageRequest;
import at.fhv.blueroute.travel.presentation.dto.VoyageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voyages")
public class VoyageController {

    private final StartVoyageService startVoyageService;
    private final GetVoyagesService getVoyagesService;
    private final JpaVoyageRepository voyageRepository;
    private final FinishVoyageService finishVoyageService;
    private final ProcessVoyageTickService processVoyageTickService;

    public VoyageController(
            StartVoyageService startVoyageService,
            GetVoyagesService getVoyagesService,
            JpaVoyageRepository voyageRepository,
            FinishVoyageService finishVoyageService,
            ProcessVoyageTickService processVoyageTickService
    ) {
        this.startVoyageService = startVoyageService;
        this.getVoyagesService = getVoyagesService;
        this.voyageRepository = voyageRepository;
        this.finishVoyageService = finishVoyageService;
        this.processVoyageTickService = processVoyageTickService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startVoyage(
            @RequestBody StartVoyageRequest request
    ) {

        try {

            Voyage voyage =
                    startVoyageService.startVoyage(
                            request.getShipId(),
                            request.getCargoId(),
                            request.getSessionId(),
                            request.getCurrentTick()
                    );

            return ResponseEntity.ok(voyage);

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/{id}/finish")
    public void finishVoyage(
            @PathVariable Long id,
            @RequestParam int currentTick
    ) {

        finishVoyageService.finishVoyage(
                id,
                currentTick
        );
    }

    @GetMapping
    public List<VoyageResponse> getVoyages(
            @RequestParam Long sessionId,
            @RequestParam int currentTick
    ) {

        return getVoyagesService
                .getAllVoyages(sessionId, currentTick);
    }

    private Voyage getVoyageById(Long id) {

        return voyageRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Voyage not found"));
    }

    @GetMapping("/pending-events")
    public boolean hasPendingEvents(
            @RequestParam Long sessionId
    ) {
        return voyageRepository
                .findBySessionId(sessionId)
                .stream()
                .anyMatch(v ->
                        v.getPendingEventType() != null
                                && !v.isEventResolved()
                );
    }

    @PostMapping("/process-tick")
    public void processTick() {
        processVoyageTickService.processTick();
    }


}