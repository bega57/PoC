package at.fhv.blueroute.voyage.presentation.dto;

public class DelayVoyageRequest {

    private int extraDelayTicks;
    private double extraFuelLoss;
    private double extraConditionLoss;

    public DelayVoyageRequest() {
    }

    public int getExtraDelayTicks() {
        return extraDelayTicks;
    }

    public double getExtraFuelLoss() {
        return extraFuelLoss;
    }

    public double getExtraConditionLoss() {
        return extraConditionLoss;
    }
}