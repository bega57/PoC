package at.fhv.blueroute.cargo.presentation.rest;

import at.fhv.blueroute.cargo.application.service.GetCargoByPortService;
import at.fhv.blueroute.cargo.domain.model.Cargo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cargo")
@CrossOrigin
public class CargoController {

    private final GetCargoByPortService service;

    public CargoController(GetCargoByPortService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cargo> getCargo(@RequestParam String portName) {
        return service.execute(portName);
    }
}