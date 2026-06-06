package at.fhv.blueroute.ship.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RepairShipRequest {

    @Min(value = 1, message = "Repair amount must be at least 1")
    @Max(value = 100, message = "Repair amount must not exceed 100")
    private int repairAmount;

    private String sessionCode;

    public int getRepairAmount() {
        return repairAmount;
    }

    public void setRepairAmount(int repairAmount) {
        this.repairAmount = repairAmount;
    }


    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }
}