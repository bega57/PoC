package at.fhv.blueroute.voyage.presentation.dto;

public class StartVoyageRequest {

    private Long shipId;
    private Long cargoId;

    public Long getShipId() {
        return shipId;
    }

    public Long getCargoId() {
        return cargoId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public void setCargoId(Long cargoId) {
        this.cargoId = cargoId;
    }
}