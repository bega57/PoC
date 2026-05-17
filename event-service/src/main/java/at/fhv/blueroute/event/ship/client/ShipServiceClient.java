package at.fhv.blueroute.event.ship.client;

import at.fhv.blueroute.event.ship.client.dto.ShipResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ShipServiceClient {

    private final RestClient restClient;

    public ShipServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${ship.service.url}") String shipServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(shipServiceUrl + "/ships")
                .build();
    }

    public ShipResponse getShip(Long shipId) {
        return restClient.get()
                .uri("/{id}", shipId)
                .retrieve()
                .body(ShipResponse.class);
    }

    public void applyDamage(Long shipId, double fuelLoss, double conditionLoss) {
        restClient.put()
                .uri("/{id}/voyage-progress?fuelLoss={fuelLoss}&conditionLoss={conditionLoss}",
                        shipId, fuelLoss, conditionLoss)
                .retrieve()
                .toBodilessEntity();
    }
}