package at.fhv.blueroute.ship.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RefuelShipRequest {

    @Min(value = 1, message = "Fuel amount must be at least 1")
    @Max(value = 100, message = "Fuel amount must not exceed 100")
    private int fuelAmount;

    private String sessionCode;

    public int getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(int fuelAmount) {
        this.fuelAmount = fuelAmount;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }
}
