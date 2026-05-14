package at.fhv.blueroute.cargo.presentation.rest;

import at.fhv.blueroute.cargo.client.CargoServiceClient;
import at.fhv.blueroute.cargo.client.dto.CargoResponse;
import at.fhv.blueroute.cargo.client.dto.CargoOfferDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cargo")
@CrossOrigin
public class CargoController {

    private final CargoServiceClient cargoServiceClient;

    public CargoController(CargoServiceClient cargoServiceClient) {
        this.cargoServiceClient = cargoServiceClient;
    }

    @GetMapping
    public List<CargoResponse> getCargo(@RequestParam String portName) {
        return cargoServiceClient.getCargo(portName);
    }

    @GetMapping("/offers")
    public List<CargoOfferDto> getAllCargoOffers() {
        return cargoServiceClient.getCargoOffers();
    }
}