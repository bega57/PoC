package at.fhv.blueroute.session.presentation.dto;

public class LeaderboardEntryResponse {
    private Long playerId;
    private String username;
    private int score;

    public LeaderboardEntryResponse(Long playerId, String username, int score) {
        this.playerId = playerId;
        this.username = username;
        this.score = score;
    }

    public Long getPlayerId() { return playerId; }
    public String getUsername() { return username; }
    public int getScore() { return score; }
}
