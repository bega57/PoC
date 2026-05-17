package at.fhv.blueroute.event.voyage.client.dto;

import at.fhv.blueroute.event.domain.model.VoyageEventType;

public class VoyageResponse {

    private Long id;
    private Long sessionId;
    private Long shipId;
    private String status;
    private VoyageEventType pendingEventType;
    private Integer eventTriggerTick;
    private Boolean eventTriggered;
    private Boolean eventResolved;
    private double reward;
    private int arrivalTick;
    private String eventResultMessage;
    private Integer extraDelayTicks;
    private Double extraFuelLoss;
    private Double extraConditionLoss;
    private Double eventCost;
    private Double rewardLossPercent;

    public VoyageResponse() {}

    public Long getId() { return id; }
    public Long getSessionId() { return sessionId; }
    public Long getShipId() { return shipId; }
    public String getStatus() { return status; }
    public VoyageEventType getPendingEventType() { return pendingEventType; }
    public Integer getEventTriggerTick() { return eventTriggerTick; }
    public boolean isEventTriggered() { return Boolean.TRUE.equals(eventTriggered); }
    public boolean isEventResolved() { return Boolean.TRUE.equals(eventResolved); }
    public double getReward() { return reward; }
    public int getArrivalTick() { return arrivalTick; }
    public String getEventResultMessage() { return eventResultMessage; }
    public Integer getExtraDelayTicks() { return extraDelayTicks; }
    public Double getExtraFuelLoss() { return extraFuelLoss; }
    public Double getExtraConditionLoss() { return extraConditionLoss; }
    public Double getEventCost() { return eventCost; }
    public Double getRewardLossPercent() { return rewardLossPercent; }
}