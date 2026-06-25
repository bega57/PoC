package at.fhv.blueroute.player.client.dto;

public class PointsUpdateRequest {

    private int amount;

    public PointsUpdateRequest() {}

    public PointsUpdateRequest(int amount) {
        this.amount = amount;
    }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
