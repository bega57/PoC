package at.fhv.blueroute.ship.client.dto;

public class StartVoyageRequest {

    private double usedCapacity;

    public StartVoyageRequest() {
    }

    public StartVoyageRequest(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }
}