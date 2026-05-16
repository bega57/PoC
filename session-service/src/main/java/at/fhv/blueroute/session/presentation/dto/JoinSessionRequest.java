package at.fhv.blueroute.session.presentation.dto;

import jakarta.validation.constraints.NotNull;

public class JoinSessionRequest {

    @NotNull(message = "Player id must not be null")
    private Long playerId;

    public JoinSessionRequest() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
}