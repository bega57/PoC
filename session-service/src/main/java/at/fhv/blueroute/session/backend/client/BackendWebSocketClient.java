package at.fhv.blueroute.session.backend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BackendWebSocketClient {

    private final RestClient restClient;

    public BackendWebSocketClient(
            RestClient.Builder builder,
            @Value("${backend.url}") String backendUrl
    ) {
        this.restClient = builder
                .baseUrl(backendUrl + "/internal/session-events")
                .build();
    }

    public void publish(String sessionCode, Object payload) {

        restClient.post()
                .uri("/{sessionCode}", sessionCode)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}