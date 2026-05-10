package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.GetUsedShipOffersService;
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
    private final GetUsedShipOffersService getUsedShipOffersService;

    public ShipController(
            ShipService shipService,
            RepairShipService repairShipService,
            RefuelShipService refuelShipService, GetUsedShipOffersService getUsedShipOffersService
    ) {
        this.shipService = shipService;
        this.repairShipService = repairShipService;
        this.refuelShipService = refuelShipService;
        this.getUsedShipOffersService = getUsedShipOffersService;
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

    @PostMapping("/sell")
    public ShipResponse sellShip(
            @RequestBody SellShipRequest request
    ) {
        return shipService.sellShip(request);
    }

    @GetMapping("/used")
    public List<UsedShipOfferResponse> getUsedShips() {

        return getUsedShipOffersService.execute()
                .stream()
                .map(offer -> new UsedShipOfferResponse(
                        offer.getId(),
                        offer.getType().name(),
                        offer.getPrice(),
                        offer.getType().getSpeed(),
                        offer.getType().getCapacity(),
                        offer.getCondition(),
                        offer.getFuelLevel()
                ))
                .toList();
    }

    @PostMapping("/used/{offerId}/buy")
    public ShipResponse buyUsedShip(
            @PathVariable Long offerId,
            @RequestBody BuyUsedShipRequest request
    ) {
        return shipService.buyUsedShip(
                offerId,
                request
        );
    }

    @GetMapping("/{shipId}/repair-cost")
    public double getRepairCost(
            @PathVariable Long shipId,
            @RequestParam int repairAmount
    ) {
        return repairShipService.calculateRepairCost(
                shipId,
                repairAmount
        );
    }

    @GetMapping("/{shipId}/refuel-cost")
    public double getRefuelCost(
            @PathVariable Long shipId,
            @RequestParam int fuelAmount
    ) {
        return refuelShipService.calculateCost(
                shipId,
                fuelAmount
        );
    }
}