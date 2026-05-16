package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.client.SessionServiceClient;
import at.fhv.blueroute.session.client.dto.SessionResponse;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SessionService {

    private final SessionServiceClient sessionServiceClient;
    private final WebSocketSender webSocketSender;

    public SessionService(SessionServiceClient sessionServiceClient,
                          WebSocketSender webSocketSender) {
        this.sessionServiceClient = sessionServiceClient;
        this.webSocketSender = webSocketSender;
    }

    public List<SessionResponse> getAllSessions() {
        return sessionServiceClient.getAllSessions();
    }
    public SessionResponse getSessionByCode(String sessionCode) {
        return sessionServiceClient.getSessionByCode(sessionCode);
    }

    public SessionResponse createSession(Long playerId, int maxPlayers) {
        return sessionServiceClient.createSession(playerId, maxPlayers);
    }

    public SessionResponse joinSession(String sessionCode, Long playerId) {

        SessionResponse response =
                sessionServiceClient.joinSession(sessionCode, playerId);

        if ("RUNNING".equals(String.valueOf(response.getStatus()))) {

            webSocketSender.sendSessionUpdate(
                    sessionCode,
                    Map.of(
                            "type", "SESSION_RUNNING",
                            "sessionCode", sessionCode,
                            "status", response.getStatus()
                    )
            );
        }

        return response;
    }

    public SessionResponse resumeSession(String sessionCode, Long playerId) {
        return sessionServiceClient.resumeSession(sessionCode, playerId);
    }

    public SessionResponse leaveSession(String sessionCode, Long playerId) {
        return sessionServiceClient.leaveSession(sessionCode, playerId);
    }

    public void heartbeat(String sessionCode, Long playerId) {
        sessionServiceClient.heartbeat(sessionCode, playerId);
    }
}