package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.client.SessionServiceClient;
import at.fhv.blueroute.session.client.dto.SessionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    private final SessionServiceClient sessionServiceClient;

    public SessionService(SessionServiceClient sessionServiceClient) {
        this.sessionServiceClient = sessionServiceClient;
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
        return sessionServiceClient.joinSession(sessionCode, playerId);
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