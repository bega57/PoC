package at.fhv.blueroute.player.presentation.rest;

import at.fhv.blueroute.player.client.ShopServiceClient;
import at.fhv.blueroute.player.client.dto.BuyPowerUpRequest;
import at.fhv.blueroute.player.client.dto.InventoryItemResponse;
import at.fhv.blueroute.player.client.dto.PowerUpItemResponse;
import at.fhv.blueroute.player.client.dto.UsePowerUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopServiceClient shopServiceClient;

    public ShopController(ShopServiceClient shopServiceClient) {
        this.shopServiceClient = shopServiceClient;
    }

    @GetMapping("/items")
    public List<PowerUpItemResponse> getShopItems() {
        return shopServiceClient.getShopItems();
    }

    @PostMapping("/buy/{playerId}")
    public ResponseEntity<?> buyPowerUp(
            @PathVariable Long playerId,
            @RequestBody BuyPowerUpRequest request
    ) {
        try {
            return ResponseEntity.ok(shopServiceClient.buyPowerUp(playerId, request.getPowerUpType()));
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("message", extractMessage(ex.getResponseBodyAsString())));
        }
    }

    private String extractMessage(String body) {
        if (body == null) return "Purchase failed";
        int start = body.indexOf("\"message\":\"");
        if (start < 0) return body;
        start += 11;
        int end = body.indexOf("\"", start);
        return end > start ? body.substring(start, end) : body;
    }

    @GetMapping("/inventory/{playerId}")
    public List<InventoryItemResponse> getInventory(@PathVariable Long playerId) {
        return shopServiceClient.getInventory(playerId);
    }

    @PostMapping("/use/{playerId}")
    public ResponseEntity<Map<String, String>> usePowerUp(
            @PathVariable Long playerId,
            @RequestBody UsePowerUpRequest request
    ) {
        Map<String, String> result = shopServiceClient.usePowerUp(playerId, request.getPowerUpType());
        return ResponseEntity.ok(result);
    }
}
