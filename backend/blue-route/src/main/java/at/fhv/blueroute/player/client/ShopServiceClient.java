package at.fhv.blueroute.player.client;

import at.fhv.blueroute.player.client.dto.BuyPowerUpRequest;
import at.fhv.blueroute.player.client.dto.InventoryItemResponse;
import at.fhv.blueroute.player.client.dto.PowerUpItemResponse;
import at.fhv.blueroute.player.client.dto.UsePowerUpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ShopServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${player.service.url}")
    private String playerServiceUrl;

    public List<PowerUpItemResponse> getShopItems() {
        String url = playerServiceUrl + "/shop/items";
        ResponseEntity<List<PowerUpItemResponse>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : List.of();
    }

    public List<InventoryItemResponse> buyPowerUp(Long playerId, String powerUpType) {
        String url = playerServiceUrl + "/shop/buy/" + playerId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BuyPowerUpRequest> entity = new HttpEntity<>(new BuyPowerUpRequest(powerUpType), headers);
        ResponseEntity<List<InventoryItemResponse>> response = restTemplate.exchange(
                url, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : List.of();
    }

    public List<InventoryItemResponse> getInventory(Long playerId) {
        String url = playerServiceUrl + "/shop/inventory/" + playerId;
        ResponseEntity<List<InventoryItemResponse>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : List.of();
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> usePowerUp(Long playerId, String powerUpType) {
        String url = playerServiceUrl + "/shop/use/" + playerId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UsePowerUpRequest> entity = new HttpEntity<>(new UsePowerUpRequest(powerUpType), headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return response.getBody() != null ? response.getBody() : Map.of();
    }
}
