package at.fhv.blueroute.ship.presentation.dto;

public class VoyageProgressRequest {

    private double fuelLoss;
    private double conditionLoss;

    public double getFuelLoss() {
        return fuelLoss;
    }

    public void setFuelLoss(double fuelLoss) {
        this.fuelLoss = fuelLoss;
    }

    public double getConditionLoss() {
        return conditionLoss;
    }

    public void setConditionLoss(double conditionLoss) {
        this.conditionLoss = conditionLoss;
    }
}