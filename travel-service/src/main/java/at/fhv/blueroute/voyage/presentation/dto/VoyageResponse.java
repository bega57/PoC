package at.fhv.blueroute.voyage.presentation.dto;

import at.fhv.blueroute.voyage.domain.model.VoyageEventType;
import at.fhv.blueroute.voyage.domain.model.Voyage;

import java.util.List;

public class VoyageResponse {

    public Long id;
    public Long sessionId;

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

    public VoyageEventType pendingEventType;
    public Integer eventTriggerTick;
    public Boolean eventTriggered;
    public Boolean eventResolved;

    public List<double[]> route;

    public String eventResultMessage;
    public Integer extraDelayTicks;
    public Double extraFuelLoss;
    public Double extraConditionLoss;
    public Double eventCost;
    public Double rewardLossPercent;

    public static VoyageResponse from(Voyage voyage, int currentTick) {

        VoyageResponse response = new VoyageResponse();

        response.id = voyage.getId();
        response.sessionId = voyage.getSessionId();

        response.shipId = voyage.getShipId();
        response.shipName = null;
        response.originPort = voyage.getOriginPort();
        response.destinationPort = voyage.getDestinationPort();

        response.status = voyage.getStatus().name();

        response.duration = voyage.getDurationInTicks();
        response.currentDay = currentTick;

        response.reward = voyage.getReward();
        response.arrivalTick = voyage.getArrivalTick();

        response.pendingEventType = voyage.getPendingEventType();
        response.eventTriggerTick = voyage.getEventTriggerTick();
        response.eventTriggered = voyage.isEventTriggered();
        response.eventResolved = voyage.isEventResolved();

        response.route = List.of();

        response.eventResultMessage = voyage.getEventResultMessage();
        response.extraDelayTicks = voyage.getExtraDelayTicks();
        response.extraFuelLoss = voyage.getExtraFuelLoss();
        response.extraConditionLoss = voyage.getExtraConditionLoss();
        response.eventCost = voyage.getEventCost();
        response.rewardLossPercent = voyage.getRewardLossPercent();

        int duration =
                Math.max(1, voyage.getDurationInTicks());

        int traveledTicks =
                Math.max(0, currentTick - voyage.getStartTick());

        response.progress =
                Math.min(
                        100,
                        (traveledTicks * 100.0) / duration
                );

        return response;
    }
}