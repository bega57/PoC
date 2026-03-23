package at.fhv.blueroute.session.presentation.dto;

import java.util.List;

public class SessionResponse {

    private Long id;
    private String sessionCode;
    private String status;
    private int currentTick;
    private int maxPlayers;
    private List<PlayerSummaryResponse> players;

    public SessionResponse() {
    }

    public SessionResponse(Long id, String sessionCode, String status, int currentTick, int maxPlayers, List<PlayerSummaryResponse> players) {
        this.id = id;
        this.sessionCode = sessionCode;
        this.status = status;
        this.currentTick = currentTick;
        this.maxPlayers = maxPlayers;
        this.players = players;
    }

    public Long getId() {
        return id;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public String getStatus() {
        return status;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<PlayerSummaryResponse> getPlayers() {
        return players;
    }
}