package at.fhv.blueroute.ship.presentation.dto;

public class BuyUsedShipRequest {

    private Long playerId;
    private String shipName;
    private String sessionCode;

    public Long getPlayerId() {
        return playerId;
    }

    public String getShipName() {
        return shipName;
    }

    public String getSessionCode() {
        return sessionCode;
    }
}