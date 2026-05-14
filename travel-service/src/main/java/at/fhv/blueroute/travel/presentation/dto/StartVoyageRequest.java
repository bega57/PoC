package at.fhv.blueroute.travel.presentation.dto;

public class StartVoyageRequest {

    private Long shipId;
    private Long cargoId;

    private String sessionCode;
    private Long sessionId;

    private int currentTick;


    public int getCurrentTick() {
        return currentTick;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getShipId() {
        return shipId;
    }

    public Long getCargoId() {
        return cargoId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public void setCargoId(Long cargoId) {
        this.cargoId = cargoId;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }
}