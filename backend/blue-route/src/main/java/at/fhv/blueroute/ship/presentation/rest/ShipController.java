package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.RefuelShipService;
import at.fhv.blueroute.ship.application.service.RepairShipService;
import at.fhv.blueroute.ship.application.service.ShipService;
import at.fhv.blueroute.ship.presentation.dto.*;
import at.fhv.blueroute.ship.application.service.GetUsedShipOffersService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import at.fhv.blueroute.websocket.WebSocketEvent;

import java.util.List;

@RestController
@RequestMapping("/ships")
@CrossOrigin(origins = "*")
public class ShipController {

    private final ShipService shipService;
    private final GetUsedShipOffersService getUsedShipOffersService;
    private final RefuelShipService refuelShipService;
    private final RepairShipService repairShipService;
    private final SimpMessagingTemplate messagingTemplate;

    public ShipController(
            ShipService shipService,
            GetUsedShipOffersService getUsedShipOffersService,
            RefuelShipService refuelShipService,
            RepairShipService repairShipService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.shipService = shipService;
        this.getUsedShipOffersService = getUsedShipOffersService;
        this.refuelShipService = refuelShipService;
        this.repairShipService = repairShipService;
        this.messagingTemplate = messagingTemplate;
    }
    @PostMapping("/buy")
    public ShipResponse buyShip(@RequestBody BuyShipRequest request) {

        ShipResponse response = shipService.buyShip(request);

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_BOUGHT")
        );

        return response;
    }


    @GetMapping("/player/{playerId}")
    public List<ShipResponse> getShipsByPlayer(@PathVariable Long playerId) {
        return shipService.getShipsByPlayer(playerId);
    }

    @PostMapping("/sell")
    public ShipResponse sellShip(@RequestBody SellShipRequest request) {

        ShipResponse response = shipService.sellShip(request);

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_SOLD")
        );

        return response;
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

        ShipResponse response =
                shipService.buyUsedShip(offerId, request);

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_BOUGHT")
        );

        return response;
    }

    @PostMapping("/{shipId}/refuel")
    public ShipResponse refuelShip(
            @PathVariable Long shipId,
            @RequestBody RefuelShipRequest request
    ) {
        ShipResponse response =
                refuelShipService.refuel(shipId, request.getFuelAmount());

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_REFUELED")
        );

        return response;
    }

    @GetMapping("/{id}/refuel-cost")
    public double getRefuelCost(
            @PathVariable Long id,
            @RequestParam int fuelAmount
    ) {
        return refuelShipService.calculateCost(id, fuelAmount);
    }

    @PostMapping("/{shipId}/repair")
    public ShipResponse repairShip(
            @PathVariable Long shipId,
            @RequestBody RepairShipRequest request
    ) {

        ShipResponse response =
                repairShipService.repair(shipId, request.getRepairAmount());

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_REPAIRED")
        );

        return response;
    }

    @GetMapping("/{id}/repair-cost")
    public double getRepairCost(
            @PathVariable Long id,
            @RequestParam int repairAmount
    ) {
        return repairShipService.calculateRepairCost(id, repairAmount);
    }

}