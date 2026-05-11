package at.fhv.blueroute.ship.presentation.dto;

public class FinishVoyageRequest {

    private String destinationPort;
    private double releasedCapacity;

    public FinishVoyageRequest() {
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public double getReleasedCapacity() {
        return releasedCapacity;
    }

    public void setReleasedCapacity(double releasedCapacity) {
        this.releasedCapacity = releasedCapacity;
    }
}