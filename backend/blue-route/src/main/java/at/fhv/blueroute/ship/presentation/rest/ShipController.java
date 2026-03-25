package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.ShipService;
import at.fhv.blueroute.ship.presentation.dto.BuyShipRequest;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ships")
@CrossOrigin(origins = "*")
public class ShipController {

    private final ShipService shipService;

    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @PostMapping("/buy")
    public ShipResponse buyShip(@RequestBody BuyShipRequest request) {
        return shipService.buyShip(request);
    }

    @GetMapping("/player/{playerId}")
    public List<ShipResponse> getShipsByPlayer(@PathVariable Long playerId) {
        return shipService.getShipsByPlayer(playerId);
    }
}