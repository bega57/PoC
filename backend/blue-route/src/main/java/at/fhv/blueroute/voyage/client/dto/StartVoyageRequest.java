package at.fhv.blueroute.voyage.client.dto;

public class StartVoyageRequest {

    private Long shipId;
    private Long cargoId;
    private Long sessionId;
    private String sessionCode;
    private int currentTick;
    private boolean smuggling;

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public Long getShipId() {
        return shipId;
    }

    public Long getCargoId() {
        return cargoId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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

    public boolean isSmuggling() {
        return smuggling;
    }

    public void setSmuggling(boolean smuggling) {
        this.smuggling = smuggling;
    }

    private String activePowerUp;

    public String getActivePowerUp() { return activePowerUp; }
    public void setActivePowerUp(String activePowerUp) { this.activePowerUp = activePowerUp; }

}
