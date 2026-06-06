package at.fhv.blueroute.player.client;

import at.fhv.blueroute.player.client.dto.BalanceUpdateRequest;
import at.fhv.blueroute.player.client.dto.PlayerResponse;
import at.fhv.blueroute.player.client.dto.PointsUpdateRequest;
import at.fhv.blueroute.player.client.dto.UpdateCompanyNameRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


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

    public void addPoints(Long playerId, int amount) {
        String url = playerServiceUrl + "/players/" + playerId + "/points";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PointsUpdateRequest> entity =
                new HttpEntity<>(new PointsUpdateRequest(amount), headers);

        try {
            restTemplate.postForObject(url, entity, Void.class);
        } catch (Exception ex) {
            System.err.println("Failed to add points for player " + playerId + ": " + ex.getMessage());
        }
    }

    public PlayerResponse getPlayer(Long playerId) {
        String url = playerServiceUrl + "/players/" + playerId;
        return restTemplate.getForObject(url, PlayerResponse.class);
    }

    public void updateCompanyName(Long playerId, String companyName) {
        String url = playerServiceUrl + "/players/" + playerId + "/company-name";

        restTemplate.postForObject(
                url,
                new UpdateCompanyNameRequest(companyName),
                Void.class
        );
    }

    public List<PlayerResponse> getAllPlayers() {
        String url = playerServiceUrl + "/players";

        PlayerResponse[] response =
                restTemplate.getForObject(url, PlayerResponse[].class);

        return response == null
                ? List.of()
                : List.of(response);
    }

}