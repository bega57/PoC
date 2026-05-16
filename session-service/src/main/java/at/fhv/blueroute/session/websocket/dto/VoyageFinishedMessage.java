package at.fhv.blueroute.session.websocket.dto;

public class VoyageFinishedMessage {

    private final Long voyageId;
    private final Long shipId;
    private final String shipName;
    private final String originPort;
    private final String destinationPort;
    private final double reward;

    public VoyageFinishedMessage(
            Long voyageId,
            Long shipId,
            String shipName,
            String originPort,
            String destinationPort,
            double reward
    ) {
        this.voyageId = voyageId;
        this.shipId = shipId;
        this.shipName = shipName;
        this.originPort = originPort;
        this.destinationPort = destinationPort;
        this.reward = reward;
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