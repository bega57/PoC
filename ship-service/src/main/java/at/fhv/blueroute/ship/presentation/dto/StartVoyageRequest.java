package at.fhv.blueroute.ship.presentation.dto;

import jakarta.validation.constraints.Min;

public class StartVoyageRequest {

    @Min(value = 0, message = "Used capacity must not be negative")
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