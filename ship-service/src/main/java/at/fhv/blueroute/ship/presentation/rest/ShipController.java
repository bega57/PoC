package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.RefuelShipService;
import at.fhv.blueroute.ship.application.service.RepairShipService;
import at.fhv.blueroute.ship.application.service.ShipService;
import at.fhv.blueroute.ship.presentation.dto.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ships")
@CrossOrigin(origins = "*")
public class ShipController {

    private final ShipService shipService;
    private final RepairShipService repairShipService;
    private final RefuelShipService refuelShipService;

    public ShipController(
            ShipService shipService,
            RepairShipService repairShipService,
            RefuelShipService refuelShipService
    ) {
        this.shipService = shipService;
        this.repairShipService = repairShipService;
        this.refuelShipService = refuelShipService;
    }

    @PostMapping("/buy")
    public ShipResponse buyShip(@RequestBody BuyShipRequest request) {
        return shipService.buyShip(request);
    }

    @GetMapping("/player/{playerId}")
    public List<ShipResponse> getShipsByPlayer(
            @PathVariable Long playerId
    ) {
        return shipService.getShipsByPlayer(playerId);
    }

    @PostMapping("/{shipId}/repair")
    public ShipResponse repairShip(
            @PathVariable Long shipId,
            @RequestBody RepairShipRequest request
    ) {
        return repairShipService.repair(
                shipId,
                request.getRepairAmount()
        );
    }

    @PostMapping("/{shipId}/refuel")
    public ShipResponse refuelShip(
            @PathVariable Long shipId,
            @RequestBody RefuelShipRequest request
    ) {
        return refuelShipService.refuel(
                shipId,
                request.getFuelAmount()
        );
    }
}