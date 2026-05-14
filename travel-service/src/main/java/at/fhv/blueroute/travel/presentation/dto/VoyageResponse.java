package at.fhv.blueroute.travel.presentation.dto;

import java.util.List;

public class VoyageResponse {

    public Long id;
    public Long shipId;
    public String shipName;

    public String originPort;
    public String destinationPort;

    public String status;

    public int duration;
    public int currentDay;
    public double progress;

    public double reward;
    public int arrivalTick;
    public List<double[]> route;

    public String eventResultMessage;
    public Integer extraDelayTicks;
    public Double extraFuelLoss;
    public Double extraConditionLoss;
    public Double eventCost;
    public Double rewardLossPercent;
}