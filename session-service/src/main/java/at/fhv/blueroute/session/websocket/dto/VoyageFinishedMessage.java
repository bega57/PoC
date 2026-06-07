package at.fhv.blueroute.session.websocket.dto;

public class VoyageFinishedMessage {

    private String type;

    private Long voyageId;
    private Long shipId;
    private String shipName;
    private String originPort;
    private String destinationPort;
    private double reward;

    // ==================== EVENT FIELDS ====================
    private String eventResultMessage;
    private Integer extraDelayTicks;
    private Double extraFuelLoss;
    private Double extraConditionLoss;
    private Double eventCost;
    private Double rewardLossPercent;
    // ======================================================

    // ==================== SMUGGLING FIELDS ====================
    private boolean smuggling;
    private double smugglingReward;
    private boolean customsChecked;
    private boolean smugglingDetected;
    private double smugglingPenalty;
    private int smugglingDetentionTicks;
    private boolean smugglingResolved;
    // ==========================================================

    private int earnedPoints;
    private String pointsBreakdown;

    public VoyageFinishedMessage(
            Long voyageId,
            Long shipId,
            String shipName,
            String originPort,
            String destinationPort,
            double reward,
            String eventResultMessage,
            Integer extraDelayTicks,
            Double extraFuelLoss,
            Double extraConditionLoss,
            Double eventCost,
            Double rewardLossPercent,
            boolean smuggling,
            double smugglingReward,
            boolean customsChecked,
            boolean smugglingDetected,
            double smugglingPenalty,
            int smugglingDetentionTicks,
            boolean smugglingResolved,
            int earnedPoints,
            String pointsBreakdown
    ) {
        this.type = "VOYAGE_FINISHED";
        this.voyageId = voyageId;
        this.shipId = shipId;
        this.shipName = shipName;
        this.originPort = originPort;
        this.destinationPort = destinationPort;
        this.reward = reward;
        this.eventResultMessage = eventResultMessage;
        this.extraDelayTicks = extraDelayTicks;
        this.extraFuelLoss = extraFuelLoss;
        this.extraConditionLoss = extraConditionLoss;
        this.eventCost = eventCost;
        this.rewardLossPercent = rewardLossPercent;
        this.smuggling = smuggling;
        this.smugglingReward = smugglingReward;
        this.customsChecked = customsChecked;
        this.smugglingDetected = smugglingDetected;
        this.smugglingPenalty = smugglingPenalty;
        this.smugglingDetentionTicks = smugglingDetentionTicks;
        this.smugglingResolved = smugglingResolved;
        this.earnedPoints = earnedPoints;
        this.pointsBreakdown = pointsBreakdown;
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

    public String getEventResultMessage() { return eventResultMessage; }
    public Integer getExtraDelayTicks() { return extraDelayTicks; }
    public Double getExtraFuelLoss() { return extraFuelLoss; }
    public Double getExtraConditionLoss() { return extraConditionLoss; }
    public Double getEventCost() { return eventCost; }
    public Double getRewardLossPercent() { return rewardLossPercent; }

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

    public int getEarnedPoints() { return earnedPoints; }
    public String getPointsBreakdown() { return pointsBreakdown; }
}
