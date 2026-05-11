package at.fhv.blueroute.ship.presentation.dto;

public class StartVoyageRequest {

    private double usedCapacity;

    public StartVoyageRequest() {
    }

    public double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }
}