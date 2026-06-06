package at.fhv.blueroute.ship.presentation.dto;

import jakarta.validation.constraints.NotNull;

public class SellShipRequest {

    @NotNull(message = "Player ID must not be null")
    private Long playerId;

    @NotNull(message = "Ship ID must not be null")
    private Long shipId;

    private String sessionCode;

    public Long getPlayerId() {
        return playerId;
    }

    public Long getShipId() {
        return shipId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }
}