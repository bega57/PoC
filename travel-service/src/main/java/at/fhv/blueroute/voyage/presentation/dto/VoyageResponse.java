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

    // ==================== SMUGGLING FIELDS ====================
    public boolean smuggling;
    public double smugglingReward;
    public boolean customsChecked;
    public boolean smugglingDetected;
    public double smugglingPenalty;
    public int smugglingDetentionTicks;
    public boolean smugglingResolved;
    // ==========================================================

    public int earnedPoints;
    public String pointsBreakdown;
    public String activePowerUp;

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
        response.currentDay = Math.min(voyage.getDurationInTicks(),
                Math.max(0, currentTick - voyage.getStartTick()));

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

        // ==================== SMUGGLING ====================
        response.smuggling = voyage.isSmuggling();
        response.smugglingReward = voyage.getSmugglingReward();
        response.customsChecked = voyage.isCustomsChecked();
        response.smugglingDetected = voyage.isSmugglingDetected();
        response.smugglingPenalty = voyage.getSmugglingPenalty();
        response.smugglingDetentionTicks = voyage.getSmugglingDetentionTicks();
        response.smugglingResolved = voyage.isSmugglingResolved();
        // ===================================================

        response.earnedPoints = voyage.getEarnedPoints();
        response.pointsBreakdown = voyage.getPointsBreakdown();
        response.activePowerUp = voyage.getActivePowerUp();

        int duration =
                Math.max(1, voyage.getDurationInTicks());

        int traveledTicks =
                Math.max(0, currentTick - voyage.getStartTick());

        response.progress =
                Math.min(1.0, (double) traveledTicks / duration);

        return response;
    }
}
