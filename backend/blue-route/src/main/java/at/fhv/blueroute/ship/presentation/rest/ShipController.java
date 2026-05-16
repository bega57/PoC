package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import at.fhv.blueroute.ship.client.dto.UsedShipOfferResponse;
import at.fhv.blueroute.ship.client.dto.*;
import at.fhv.blueroute.websocket.WebSocketEvent;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ships")
@CrossOrigin(origins = "*")
public class ShipController {

    private final ShipServiceClient shipServiceClient;
    private final SimpMessagingTemplate messagingTemplate;

    public ShipController(
            ShipServiceClient shipServiceClient,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.shipServiceClient = shipServiceClient;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/buy")
    public ShipResponse buyShip(@RequestBody BuyShipRequest request) {

        ShipResponse response =
                shipServiceClient.buyShip(request);

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_BOUGHT")
        );

        return response;
    }

    @GetMapping("/player/{playerId}")
    public List<ShipResponse> getShipsByPlayer(
            @PathVariable Long playerId
    ) {
        return shipServiceClient.getShipsByPlayer(playerId);
    }

    @PostMapping("/sell")
    public ShipResponse sellShip(
            @RequestBody SellShipRequest request
    ) {

        ShipResponse response =
                shipServiceClient.sellShip(request);

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_SOLD")
        );

        return response;
    }

    @GetMapping("/used")
    public List<UsedShipOfferResponse> getUsedShips() {
        return shipServiceClient.getUsedShips();
    }

    @PostMapping("/used/{offerId}/buy")
    public ShipResponse buyUsedShip(
            @PathVariable Long offerId,
            @RequestBody BuyUsedShipRequest request
    ) {

        ShipResponse response =
                shipServiceClient.buyUsedShip(
                        offerId,
                        request
                );

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_BOUGHT")
        );

        return response;
    }

    @PostMapping("/{shipId}/repair")
    public ShipResponse repairShip(
            @PathVariable Long shipId,
            @RequestBody RepairShipRequest request
    ) {

        ShipResponse response =
                shipServiceClient.repairShip(
                        shipId,
                        request
                );

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_REPAIRED")
        );

        return response;
    }

    @PostMapping("/{shipId}/refuel")
    public ShipResponse refuelShip(
            @PathVariable Long shipId,
            @RequestBody RefuelShipRequest request
    ) {

        ShipResponse response =
                shipServiceClient.refuelShip(
                        shipId,
                        request
                );

        messagingTemplate.convertAndSend(
                "/topic/session/" + request.getSessionCode(),
                new WebSocketEvent("SHIP_REFUELED")
        );

        return response;
    }

    @GetMapping("/{shipId}/repair-cost")
    public double getRepairCost(
            @PathVariable Long shipId,
            @RequestParam int repairAmount
    ) {
        return shipServiceClient.getRepairCost(
                shipId,
                repairAmount
        );
    }

    @GetMapping("/{shipId}/refuel-cost")
    public double getRefuelCost(
            @PathVariable Long shipId,
            @RequestParam int fuelAmount
    ) {
        return shipServiceClient.getRefuelCost(
                shipId,
                fuelAmount
        );
    }

    @GetMapping("/{shipId}")
    public ShipResponse getShip(
            @PathVariable Long shipId
    ) {
        return shipServiceClient.getShip(shipId);
    }

    @GetMapping
    public List<ShipResponse> getAllShips() {
        return shipServiceClient.getAllShips();
    }
}