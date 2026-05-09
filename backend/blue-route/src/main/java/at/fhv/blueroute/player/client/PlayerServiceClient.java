package at.fhv.blueroute.player.client;

import at.fhv.blueroute.player.client.dto.BalanceUpdateRequest;
import at.fhv.blueroute.player.client.dto.PlayerResponse;
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

        try {
            restTemplate.postForObject(
                    url,
                    entity,
                    Void.class
            );
        } catch (org.springframework.web.client.HttpClientErrorException.BadRequest ex) {
            throw new RuntimeException("Player service rejected balance update: " + ex.getResponseBodyAsString());
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Player not found in player service: " + playerId);
        } catch (org.springframework.web.client.ResourceAccessException ex) {
            throw new RuntimeException("Player service is currently not reachable.");
        }
    }

    public PlayerResponse getPlayer(Long playerId) {
        String url = playerServiceUrl + "/players/" + playerId;
        return restTemplate.getForObject(url, PlayerResponse.class);
    }
}