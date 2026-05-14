package at.fhv.blueroute.player.client;

import at.fhv.blueroute.player.client.dto.BalanceUpdateRequest;
import at.fhv.blueroute.player.client.dto.PlayerResponse;
import at.fhv.blueroute.player.client.dto.UpdateCompanyNameRequest;
import at.fhv.blueroute.player.presentation.dto.PlayerRequest;
import at.fhv.blueroute.player.presentation.dto.SelectPortRequest;

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

    public PlayerResponse getPlayer(Long playerId) {
        String url = playerServiceUrl + "/players/" + playerId;
        return restTemplate.getForObject(url, PlayerResponse.class);
    }

    public PlayerResponse updateCompanyName(Long playerId, String companyName) {
        String url = playerServiceUrl + "/players/" + playerId + "/company-name";

        return restTemplate.postForObject(
                url,
                new UpdateCompanyNameRequest(companyName),
                PlayerResponse.class
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

    public PlayerResponse createPlayer(PlayerRequest request) {
        String url = playerServiceUrl + "/players";

        return restTemplate.postForObject(
                url,
                request,
                PlayerResponse.class
        );
    }

    public PlayerResponse selectPort(Long playerId, String port) {
        String url = playerServiceUrl + "/players/" + playerId + "/port";

        SelectPortRequest request = new SelectPortRequest();
        request.setPort(port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SelectPortRequest> entity =
                new HttpEntity<>(request, headers);

        return restTemplate.postForObject(
                url,
                entity,
                PlayerResponse.class
        );
    }
}