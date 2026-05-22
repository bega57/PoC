package at.fhv.blueroute.session.websocket.dto;

public class VoyageFinishedMessage {

    private String type;

    private Long voyageId;
    private Long shipId;
    private String shipName;
    private String originPort;
    private String destinationPort;
    private double reward;

    // ==================== SMUGGLING FIELDS ====================
    private boolean smuggling;
    private double smugglingReward;
    private boolean customsChecked;
    private boolean smugglingDetected;
    private double smugglingPenalty;
    private int smugglingDetentionTicks;
    private boolean smugglingResolved;
    // ==========================================================

    public VoyageFinishedMessage(
            Long voyageId,
            Long shipId,
            String shipName,
            String originPort,
            String destinationPort,
            double reward,
            boolean smuggling,
            double smugglingReward,
            boolean customsChecked,
            boolean smugglingDetected,
            double smugglingPenalty,
            int smugglingDetentionTicks,
            boolean smugglingResolved
    ) {
        this.type = "VOYAGE_FINISHED";
        this.voyageId = voyageId;
        this.shipId = shipId;
        this.shipName = shipName;
        this.originPort = originPort;
        this.destinationPort = destinationPort;
        this.reward = reward;
        this.smuggling = smuggling;
        this.smugglingReward = smugglingReward;
        this.customsChecked = customsChecked;
        this.smugglingDetected = smugglingDetected;
        this.smugglingPenalty = smugglingPenalty;
        this.smugglingDetentionTicks = smugglingDetentionTicks;
        this.smugglingResolved = smugglingResolved;
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

    public boolean isSmuggling() {
        return smuggling;
    }

    public double getSmugglingReward() {
        return smugglingReward;
    }

    public boolean isCustomsChecked() {
        return customsChecked;
    }

    public boolean isSmugglingDetected() {
        return smugglingDetected;
    }

    public double getSmugglingPenalty() {
        return smugglingPenalty;
    }

    public int getSmugglingDetentionTicks() {
        return smugglingDetentionTicks;
    }

    public boolean isSmugglingResolved() {
        return smugglingResolved;
    }
}
