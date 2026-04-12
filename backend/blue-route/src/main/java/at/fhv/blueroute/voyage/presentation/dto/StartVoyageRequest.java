package at.fhv.blueroute.voyage.presentation.dto;

public class StartVoyageRequest {

    private Long shipId;
    private Long cargoId;

    private String sessionCode;


    public Long getShipId() {
        return shipId;
    }

    public Long getCargoId() {
        return cargoId;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public void setCargoId(Long cargoId) {
        this.cargoId = cargoId;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

}