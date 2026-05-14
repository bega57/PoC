package at.fhv.blueroute.cargo.presentation.rest;

import at.fhv.blueroute.cargo.application.service.GetCargoByPortService;
import at.fhv.blueroute.cargo.application.service.GetCargoOffersService;
import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.presentation.dto.CargoOfferDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cargo")
@CrossOrigin
public class CargoController {

    private final GetCargoByPortService service;
    private final GetCargoOffersService cargoOffersService;

    public CargoController(GetCargoByPortService service,
                           GetCargoOffersService cargoOffersService) {
        this.service = service;
        this.cargoOffersService = cargoOffersService;
    }

    @GetMapping
    public List<Cargo> getCargo(@RequestParam String portName) {
        return service.execute(portName);
    }

    @GetMapping("/offers")
    public List<CargoOfferDto> getAllCargoOffers() {
        return cargoOffersService.execute();
    }
}