package at.fhv.blueroute.session.voyage.client.dto;

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

    private List<double[]> route;

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

    public List<double[]> getRoute() {
        return route;
    }
}