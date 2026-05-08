package at.fhv.blueroute.player.client;

import at.fhv.blueroute.player.client.dto.BalanceUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PlayerServiceClient {

    private final RestTemplate restTemplate;

    @Value("${player.service.url}")
    private String playerServiceUrl;

    public PlayerServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    public void updateBalance(Long playerId, Double amount, String reason) {

        String url = playerServiceUrl + "/players/" + playerId + "/balance";

        BalanceUpdateRequest request =
                new BalanceUpdateRequest(amount, reason);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BalanceUpdateRequest> entity =
                new HttpEntity<>(request, headers);

        restTemplate.postForObject(
                url,
                entity,
                Void.class
        );
    }
}