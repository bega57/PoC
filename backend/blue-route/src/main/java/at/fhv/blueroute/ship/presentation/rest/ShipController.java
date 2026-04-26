package at.fhv.blueroute.ship.presentation.rest;

import at.fhv.blueroute.ship.application.service.ShipService;
import at.fhv.blueroute.ship.presentation.dto.BuyShipRequest;
import at.fhv.blueroute.ship.presentation.dto.SellShipRequest;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import at.fhv.blueroute.ship.application.service.GetUsedShipOffersService;
import at.fhv.blueroute.ship.presentation.dto.UsedShipOfferResponse;
import at.fhv.blueroute.ship.presentation.dto.BuyUsedShipRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ships")
@CrossOrigin(origins = "*")
public class ShipController {

    private final ShipService shipService;
    private final GetUsedShipOffersService getUsedShipOffersService;

    public ShipController(
            ShipService shipService,
            GetUsedShipOffersService getUsedShipOffersService
    ) {
        this.shipService = shipService;
        this.getUsedShipOffersService = getUsedShipOffersService;
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

}