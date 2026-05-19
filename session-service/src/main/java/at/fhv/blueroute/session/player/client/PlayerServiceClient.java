package at.fhv.blueroute.session.player.client;

import at.fhv.blueroute.session.player.client.dto.PlayerResponse;
import org.springframework.beans.factory.annotation.Value;
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

    public PlayerResponse getPlayer(Long playerId) {
        return restClient.get()
                .uri("/{id}", playerId)
                .retrieve()
                .body(PlayerResponse.class);
    }
}