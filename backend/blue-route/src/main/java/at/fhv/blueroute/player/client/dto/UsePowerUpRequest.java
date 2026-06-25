package at.fhv.blueroute.player.client.dto;

public class UsePowerUpRequest {

    private String powerUpType;

    public UsePowerUpRequest() {}
    public UsePowerUpRequest(String powerUpType) { this.powerUpType = powerUpType; }

    public String getPowerUpType() { return powerUpType; }
    public void setPowerUpType(String powerUpType) { this.powerUpType = powerUpType; }
}
