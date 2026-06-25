package at.fhv.blueroute.player.presentation.rest;

import at.fhv.blueroute.player.application.service.ShopService;
import at.fhv.blueroute.player.presentation.dto.BuyPowerUpRequest;
import at.fhv.blueroute.player.presentation.dto.InventoryItemResponse;
import at.fhv.blueroute.player.presentation.dto.PowerUpItemResponse;
import at.fhv.blueroute.player.presentation.dto.UsePowerUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/items")
    public List<PowerUpItemResponse> getShopItems() {
        return shopService.getShopItems();
    }

    @PostMapping("/buy/{playerId}")
    public List<InventoryItemResponse> buyPowerUp(
            @PathVariable Long playerId,
            @RequestBody BuyPowerUpRequest request
    ) {
        return shopService.buyPowerUp(playerId, request.getPowerUpType());
    }

    @GetMapping("/inventory/{playerId}")
    public List<InventoryItemResponse> getInventory(@PathVariable Long playerId) {
        return shopService.getInventory(playerId);
    }

    @PostMapping("/use/{playerId}")
    public ResponseEntity<Map<String, String>> usePowerUp(
            @PathVariable Long playerId,
            @RequestBody UsePowerUpRequest request
    ) {
        String effectType = shopService.usePowerUp(playerId, request.getPowerUpType());
        return ResponseEntity.ok(Map.of(
                "effectType", effectType,
                "powerUpType", request.getPowerUpType()
        ));
    }
}
