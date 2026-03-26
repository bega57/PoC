package at.fhv.blueroute.player.presentation.dto;

public class SelectPortRequest {

    private Long playerId;
    private String port;

    public SelectPortRequest() {}

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}