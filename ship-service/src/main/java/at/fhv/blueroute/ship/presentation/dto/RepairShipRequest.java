package at.fhv.blueroute.ship.presentation.dto;

public class RepairShipRequest {

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