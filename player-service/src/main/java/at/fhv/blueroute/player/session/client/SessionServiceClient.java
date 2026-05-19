package at.fhv.blueroute.player.session.client;

import at.fhv.blueroute.player.session.client.dto.SessionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SessionServiceClient {

    private final RestClient restClient;

    public SessionServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${session.service.url}") String sessionServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(sessionServiceUrl + "/sessions")
                .build();
    }

    public SessionResponse getSessionByCode(String sessionCode) {
        return restClient.get()
                .uri("/{sessionCode}", sessionCode)
                .retrieve()
                .body(SessionResponse.class);
    }
}