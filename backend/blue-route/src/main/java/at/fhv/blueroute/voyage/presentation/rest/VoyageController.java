package at.fhv.blueroute.voyage.presentation.rest;

import at.fhv.blueroute.voyage.application.service.FinishVoyageService;
import at.fhv.blueroute.voyage.application.service.GetVoyagesService;
import at.fhv.blueroute.voyage.application.service.StartVoyageService;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.presentation.dto.StartVoyageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voyages")
public class VoyageController {

    private final StartVoyageService startVoyageService;
    private final GetVoyagesService getVoyagesService;
    private final FinishVoyageService finishVoyageService;

    public VoyageController(StartVoyageService startVoyageService,
                            GetVoyagesService getVoyagesService, FinishVoyageService finishVoyageService) {
        this.startVoyageService = startVoyageService;
        this.getVoyagesService = getVoyagesService;
        this.finishVoyageService = finishVoyageService;
    }

    @PostMapping("/start")
    public Voyage startVoyage(@RequestBody StartVoyageRequest request) {
        return startVoyageService.startVoyage(
                request.getShipId(),
                request.getCargoId()
        );
    }

    @PostMapping("/{id}/finish")
    public void finish(@PathVariable Long id) {
        finishVoyageService.finishVoyage(id);
    }

    @GetMapping
    public List<Voyage> getVoyages() {
        return getVoyagesService.getAllVoyages();
    }
}