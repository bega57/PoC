package at.fhv.blueroute.event.player.client;

import at.fhv.blueroute.event.player.client.dto.BalanceUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PlayerServiceClient {

    private final RestClient restClient;

    public PlayerServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${player.service.url}") String playerServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(playerServiceUrl + "/players")
                .build();
    }

    public void updateBalance(Long playerId, double amount, String reason) {
        restClient.post()
                .uri("/{id}/balance", playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BalanceUpdateRequest(amount, reason))
                .retrieve()
                .toBodilessEntity();
    }
}