package at.fhv.blueroute.voyage.presentation.dto;

public class StartVoyageRequest {

    private Long shipId;
    private Long cargoId;

    private String sessionCode;
    private Long sessionId;

    private int currentTick;

    private boolean smuggling;

    private String activePowerUp;

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

    public boolean isSmuggling() {
        return smuggling;
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

    public void setSmuggling(boolean smuggling) {
        this.smuggling = smuggling;
    }

    public String getActivePowerUp() { return activePowerUp; }
    public void setActivePowerUp(String activePowerUp) { this.activePowerUp = activePowerUp; }
}
