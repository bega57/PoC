package at.fhv.blueroute.player.client.dto;

public class BuyPowerUpRequest {

    private String powerUpType;

    public BuyPowerUpRequest() {}
    public BuyPowerUpRequest(String powerUpType) { this.powerUpType = powerUpType; }

    public String getPowerUpType() { return powerUpType; }
    public void setPowerUpType(String powerUpType) { this.powerUpType = powerUpType; }
}
