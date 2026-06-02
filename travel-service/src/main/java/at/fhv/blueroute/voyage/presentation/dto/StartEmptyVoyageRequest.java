package at.fhv.blueroute.voyage.presentation.dto;

public class StartEmptyVoyageRequest {

    private Long shipId;
    private String destinationPortName;
    private Long sessionId;
    private int currentTick;

    public Long getShipId() { return shipId; }
    public void setShipId(Long shipId) { this.shipId = shipId; }

    public String getDestinationPortName() { return destinationPortName; }
    public void setDestinationPortName(String destinationPortName) { this.destinationPortName = destinationPortName; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public int getCurrentTick() { return currentTick; }
    public void setCurrentTick(int currentTick) { this.currentTick = currentTick; }
}
