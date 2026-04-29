package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.RefuelShipService;
import at.fhv.blueroute.ship.application.service.RepairShipService;
import at.fhv.blueroute.ship.application.service.ShipService;
import at.fhv.blueroute.ship.presentation.dto.*;
import at.fhv.blueroute.ship.application.service.GetUsedShipOffersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ships")
@CrossOrigin(origins = "*")
public class ShipController {

    private final ShipService shipService;
    private final GetUsedShipOffersService getUsedShipOffersService;
    private final RefuelShipService refuelShipService;
    private final RepairShipService repairShipService;

    public ShipController(
            ShipService shipService,
            GetUsedShipOffersService getUsedShipOffersService,
            RefuelShipService refuelShipService,
            RepairShipService repairShipService
    ) {
        this.shipService = shipService;
        this.getUsedShipOffersService = getUsedShipOffersService;
        this.refuelShipService = refuelShipService;
        this.repairShipService = repairShipService;
    }
    @PostMapping("/buy")
    public ShipResponse buyShip(@RequestBody BuyShipRequest request) {
        return shipService.buyShip(request);
    }

    @GetMapping("/player/{playerId}")
    public List<ShipResponse> getShipsByPlayer(@PathVariable Long playerId) {
        return shipService.getShipsByPlayer(playerId);
    }

    @PostMapping("/sell")
    public ShipResponse sellShip(@RequestBody SellShipRequest request) {
        return shipService.sellShip(request);
    }

    @GetMapping("/used/{sessionCode}")
    public List<UsedShipOfferResponse> getUsedShips(@PathVariable String sessionCode) {
        return getUsedShipOffersService.execute(sessionCode)
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
        return shipService.buyUsedShip(offerId, request);
    }

    @PostMapping("/{shipId}/refuel")
    public ShipResponse refuelShip(
            @PathVariable Long shipId,
            @RequestBody RefuelShipRequest request
    ) {
        return refuelShipService.refuel(shipId, request.getFuelAmount());
    }

    @GetMapping("/{id}/refuel-cost")
    public double getRefuelCost(
            @PathVariable Long id,
            @RequestParam int fuelAmount
    ) {
        return refuelShipService.calculateCost(id, fuelAmount);
    }

    @PostMapping("/{shipId}/repair")
    public ShipResponse repairShip(@PathVariable Long shipId) {
        return repairShipService.repair(shipId);
    }

}