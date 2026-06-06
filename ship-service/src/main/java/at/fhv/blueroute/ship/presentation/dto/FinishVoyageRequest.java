package at.fhv.blueroute.ship.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FinishVoyageRequest {

    @NotBlank(message = "Destination port must not be blank")
    @Size(max = 128, message = "Destination port must not exceed 128 characters")
    private String destinationPort;

    @Min(value = 0, message = "Released capacity must not be negative")
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