package at.fhv.blueroute.session.client;

import at.fhv.blueroute.session.client.dto.CreateSessionRequest;
import at.fhv.blueroute.session.client.dto.JoinSessionRequest;
import at.fhv.blueroute.session.client.dto.SessionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

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

    public List<SessionResponse> getAllSessions() {
        return List.of(restClient.get()
                .retrieve()
                .body(SessionResponse[].class));
    }

    public SessionResponse getSessionByCode(String sessionCode) {
        return restClient.get()
                .uri("/{sessionCode}", sessionCode)
                .retrieve()
                .body(SessionResponse.class);
    }

    public SessionResponse createSession(Long playerId, int maxPlayers) {

        CreateSessionRequest request = new CreateSessionRequest();
        request.setPlayerId(playerId);
        request.setMaxPlayers(maxPlayers);

        return restClient.post()
                .body(request)
                .retrieve()
                .body(SessionResponse.class);
    }

    public SessionResponse joinSession(String sessionCode, Long playerId) {

        JoinSessionRequest request = new JoinSessionRequest();
        request.setPlayerId(playerId);

        return restClient.post()
                .uri("/{sessionCode}/join", sessionCode)
                .body(request)
                .retrieve()
                .body(SessionResponse.class);
    }

    public SessionResponse resumeSession(String sessionCode, Long playerId) {

        JoinSessionRequest request = new JoinSessionRequest();
        request.setPlayerId(playerId);

        return restClient.post()
                .uri("/{sessionCode}/resume", sessionCode)
                .body(request)
                .retrieve()
                .body(SessionResponse.class);
    }

    public SessionResponse leaveSession(String sessionCode, Long playerId) {

        JoinSessionRequest request = new JoinSessionRequest();
        request.setPlayerId(playerId);

        return restClient.post()
                .uri("/{sessionCode}/leave", sessionCode)
                .body(request)
                .retrieve()
                .body(SessionResponse.class);
    }

    public void heartbeat(String sessionCode, Long playerId) {
        restClient.patch()
                .uri("/{sessionCode}/players/{playerId}/heartbeat", sessionCode, playerId)
                .retrieve()
                .toBodilessEntity();
    }
}