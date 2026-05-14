package at.fhv.blueroute.travel.client.dto;

public class DelayVoyageRequest {

    private int extraDelayTicks;
    private double extraFuelLoss;
    private double extraConditionLoss;

    public DelayVoyageRequest() {
    }

    public DelayVoyageRequest(int extraDelayTicks, double extraFuelLoss, double extraConditionLoss) {
        this.extraDelayTicks = extraDelayTicks;
        this.extraFuelLoss = extraFuelLoss;
        this.extraConditionLoss = extraConditionLoss;
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