package at.fhv.blueroute.ship.presentation.dto;

public class SellShipRequest {

    private Long playerId;
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