package at.fhv.blueroute.voyage.client.dto;

import java.util.List;

public class VoyageResponse {

    private Long id;
    private Long sessionId;

    private Long shipId;
    private String shipName;

    private String originPort;
    private String destinationPort;

    private String status;

    private int duration;
    private int currentDay;
    private int arrivalTick;

    private double progress;

    private double reward;

    private Boolean eventResolved;

    private List<double[]> route;

    private String eventResultMessage;
    private Integer extraDelayTicks;
    private Double extraFuelLoss;
    private Double extraConditionLoss;
    private Double eventCost;
    private Double rewardLossPercent;
    private Boolean eventTriggered;
    private Integer eventTriggerTick;

    public VoyageResponse() {
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
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

    public String getStatus() {
        return status;
    }

    public int getDuration() {
        return duration;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public int getArrivalTick() {
        return arrivalTick;
    }

    public double getProgress() {
        return progress;
    }

    public double getReward() {
        return reward;
    }

    public boolean isEventResolved() {
        return eventResolved != null && eventResolved;
    }

    public List<double[]> getRoute() {
        return route;
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

    public boolean isEventTriggered() {
        return eventTriggered != null && eventTriggered;
    }

    public Integer getEventTriggerTick() {
        return eventTriggerTick;
    }
}