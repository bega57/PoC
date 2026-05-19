package at.fhv.blueroute.session.websocket.dto;

public class VoyageFinishedMessage {

    private String type;

    private Long voyageId;
    private Long shipId;
    private String shipName;
    private String originPort;
    private String destinationPort;
    private double reward;

    public VoyageFinishedMessage(
            Long voyageId,
            Long shipId,
            String shipName,
            String originPort,
            String destinationPort,
            double reward
    ) {
        this.type = "VOYAGE_FINISHED";
        this.voyageId = voyageId;
        this.shipId = shipId;
        this.shipName = shipName;
        this.originPort = originPort;
        this.destinationPort = destinationPort;
        this.reward = reward;
    }

    public String getType() {
        return type;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public Long getShipId() {
        return shipId;
    }

    public String getShipName() {
        return shipName;
    }

    public String getOriginPort() {
        return originPort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public double getReward() {
        return reward;
    }
}