package at.fhv.blueroute.common.websocket;

public class VoyageFinishedMessage {

    private String type;
    private String sessionCode;
    private Long voyageId;
    private Long shipId;
    private String destinationPort;
    private double reward;
    private String eventResultMessage;
    private Integer extraDelayTicks;
    private Double extraFuelLoss;
    private Double extraConditionLoss;
    private Double eventCost;
    private Double rewardLossPercent;

    public VoyageFinishedMessage() {
    }

    public VoyageFinishedMessage(
            String type,
            String sessionCode,
            Long voyageId,
            Long shipId,
            String destinationPort,
            double reward,
            String eventResultMessage,
            Integer extraDelayTicks,
            Double extraFuelLoss,
            Double extraConditionLoss,
            Double eventCost,
            Double rewardLossPercent
    ) {
        this.type = type;
        this.sessionCode = sessionCode;
        this.voyageId = voyageId;
        this.shipId = shipId;
        this.destinationPort = destinationPort;
        this.reward = reward;
        this.eventResultMessage = eventResultMessage;
        this.extraDelayTicks = extraDelayTicks;
        this.extraFuelLoss = extraFuelLoss;
        this.extraConditionLoss = extraConditionLoss;
        this.eventCost = eventCost;
        this.rewardLossPercent = rewardLossPercent;
    }

    public String getType() {
        return type;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public Long getShipId() {
        return shipId;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public double getReward() {
        return reward;
    }

    public String getEventResultMessage() {
        return eventResultMessage;
    }

    public Integer getExtraDelayTicks() {
        return extraDelayTicks;
    }

    public Double getExtraFuelLoss() {
        return extraFuelLoss;
    }

    public Double getExtraConditionLoss() {
        return extraConditionLoss;
    }

    public Double getEventCost() {
        return eventCost;
    }

    public Double getRewardLossPercent() {
        return rewardLossPercent;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }
}