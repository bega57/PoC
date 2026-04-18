package at.fhv.blueroute.voyage.presentation.rest;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.voyage.application.service.FinishVoyageService;
import at.fhv.blueroute.voyage.application.service.GetVoyagesService;
import at.fhv.blueroute.voyage.application.service.StartVoyageService;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.presentation.dto.StartVoyageRequest;
import at.fhv.blueroute.voyage.presentation.dto.VoyageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voyages")
public class VoyageController {

    private final StartVoyageService startVoyageService;
    private final GetVoyagesService getVoyagesService;
    private final FinishVoyageService finishVoyageService;
    private final JpaSessionRepository sessionRepository;

    public VoyageController(StartVoyageService startVoyageService,
                            GetVoyagesService getVoyagesService, FinishVoyageService finishVoyageService, JpaSessionRepository sessionRepository) {
        this.startVoyageService = startVoyageService;
        this.getVoyagesService = getVoyagesService;
        this.finishVoyageService = finishVoyageService;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startVoyage(@RequestBody StartVoyageRequest request) {

        System.out.println("🚨 CONTROLLER WIRD AUSGEFÜHRT 🚨");
        try {
            Voyage voyage = startVoyageService.startVoyage(
                    request.getShipId(),
                    request.getCargoId(),
                    request.getSessionCode()
            );
            return ResponseEntity.ok(voyage);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/finish")
    public void finish(@PathVariable Long id) {
        finishVoyageService.finishVoyage(id);
    }

    @GetMapping
    public List<VoyageResponse> getVoyages(@RequestParam Long sessionId) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow();

        return getVoyagesService.getAllVoyages(session);
    }
}