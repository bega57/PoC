package at.fhv.blueroute.event.session.client;

import at.fhv.blueroute.event.session.client.dto.SessionStatusUpdateRequest;
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

    public void resumeAfterEvent(Long sessionId) {
        restClient.post()
                .uri("/by-id/{sessionId}/event-resolved", sessionId)
                .body(new SessionStatusUpdateRequest("RUNNING"))
                .retrieve()
                .toBodilessEntity();
    }
}