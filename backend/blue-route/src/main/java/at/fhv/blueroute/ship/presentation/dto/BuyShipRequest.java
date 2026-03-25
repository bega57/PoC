package at.fhv.blueroute.ship.presentation.dto;

public class BuyShipRequest {

    private Long playerId;
    private String companyName;
    private String shipName;
    private String shipType;

    public BuyShipRequest() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getShipName() {
        return shipName;
    }

    public String getShipType() {
        return shipType;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

}