package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.GetUsedShipOffersService;
import at.fhv.blueroute.ship.application.service.RefuelShipService;
import at.fhv.blueroute.ship.application.service.RepairShipService;
import at.fhv.blueroute.ship.application.service.ShipService;
import at.fhv.blueroute.ship.presentation.dto.*;

import jakarta.validation.Valid;
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
    public ShipResponse buyShip(@Valid @RequestBody BuyShipRequest request) {
        return shipService.buyShip(request);
    }

    @GetMapping("/player/{playerId}")
    public List<ShipResponse> getShipsByPlayer(
            @PathVariable Long playerId
    ) {
        return shipService.getShipsByPlayer(playerId);
    }

    @GetMapping
    public List<ShipResponse> getAllShips() {
        return shipService.getAllShips();
    }

    @GetMapping("/{shipId}")
    public ShipResponse getShip(
            @PathVariable Long shipId
    ) {
        return shipService.getShip(shipId);
    }

    @PostMapping("/{shipId}/repair")
    public ShipResponse repairShip(
            @PathVariable Long shipId,
            @Valid @RequestBody RepairShipRequest request
    ) {
        return repairShipService.repair(
                shipId,
                request.getRepairAmount()
        );
    }

    @PostMapping("/{shipId}/refuel")
    public ShipResponse refuelShip(
            @PathVariable Long shipId,
            @Valid @RequestBody RefuelShipRequest request
    ) {
        return refuelShipService.refuel(
                shipId,
                request.getFuelAmount()
        );
    }

    @PostMapping("/sell")
    public ShipResponse sellShip(
            @Valid @RequestBody SellShipRequest request
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
            @Valid @RequestBody BuyUsedShipRequest request
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

    @PostMapping("/{shipId}/start-voyage")
    public void startVoyage(
            @PathVariable Long shipId,
            @Valid @RequestBody StartVoyageRequest request
    ) {
        shipService.startVoyage(
                shipId,
                request.getUsedCapacity()
        );
    }

    @PostMapping("/{shipId}/finish-voyage")
    public void finishVoyage(
            @PathVariable Long shipId,
            @Valid @RequestBody FinishVoyageRequest request
    ) {
        shipService.finishVoyage(
                shipId,
                request.getDestinationPort(),
                request.getReleasedCapacity()
        );
    }


    @PutMapping("/{id}/voyage-progress")
    public void updateVoyageProgress(
            @PathVariable Long id,
            @RequestParam double fuelLoss,
            @RequestParam double conditionLoss
    ) {

        shipService.updateVoyageProgress(
                id,
                fuelLoss,
                conditionLoss
        );
    }

    @PostMapping("/player/{playerId}/assign-port")
    public void assignPortToUnlocatedShips(
            @PathVariable Long playerId,
            @RequestParam String port
    ) {
        shipService.assignPortToUnlocatedShips(playerId, port);
    }
}