package at.fhv.blueroute.common.websocket;

import at.fhv.blueroute.session.presentation.dto.LeaderboardEntryResponse;

import java.util.List;

public class LeaderboardMessage {

    private String type;
    private String sessionCode;
    private List<LeaderboardEntryResponse> leaderboard;

    public LeaderboardMessage(String type, String sessionCode, List<LeaderboardEntryResponse> leaderboard) {
        this.type = type;
        this.sessionCode = sessionCode;
        this.leaderboard = leaderboard;
    }

    public String getType() { return type; }
    public String getSessionCode() { return sessionCode; }
    public List<LeaderboardEntryResponse> getLeaderboard() { return leaderboard; }
}