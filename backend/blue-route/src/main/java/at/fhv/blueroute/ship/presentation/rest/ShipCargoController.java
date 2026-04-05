package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.LoadCargoService;
import at.fhv.blueroute.ship.presentation.dto.LoadCargoRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cargo")
public class ShipCargoController {

    private final LoadCargoService loadCargoService;

    public ShipCargoController(LoadCargoService loadCargoService) {
        this.loadCargoService = loadCargoService;
    }

    @PostMapping("/load")
    public void loadCargo(@RequestBody LoadCargoRequest request) {
        loadCargoService.loadCargo(request);
    }
}