package at.fhv.blueroute.common.websocket;

public class VoyageStartedMessage {

    private String type;
    private String sessionCode;
    private Long shipId;
    private String originPort;
    private String destinationPort;
    private int duration;

    public VoyageStartedMessage() {}

    public VoyageStartedMessage(String type, String sessionCode, Long shipId,
                                String originPort, String destinationPort, int duration) {
        this.type = type;
        this.sessionCode = sessionCode;
        this.shipId = shipId;
        this.originPort = originPort;
        this.destinationPort = destinationPort;
        this.duration = duration;
    }

    public String getType() { return type; }
    public String getSessionCode() { return sessionCode; }
    public Long getShipId() { return shipId; }
    public String getOriginPort() { return originPort; }
    public String getDestinationPort() { return destinationPort; }
    public int getDuration() { return duration; }

    public void setType(String type) { this.type = type; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public void setShipId(Long shipId) { this.shipId = shipId; }
    public void setOriginPort(String originPort) { this.originPort = originPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }
    public void setDuration(int duration) { this.duration = duration; }
}