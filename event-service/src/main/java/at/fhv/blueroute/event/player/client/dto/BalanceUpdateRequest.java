package at.fhv.blueroute.event.player.client.dto;

public class BalanceUpdateRequest {

    private Double amount;
    private String reason;

    public BalanceUpdateRequest() {}

    public BalanceUpdateRequest(Double amount, String reason) {
        this.amount = amount;
        this.reason = reason;
    }

    public Double getAmount() { return amount; }
    public String getReason() { return reason; }
}