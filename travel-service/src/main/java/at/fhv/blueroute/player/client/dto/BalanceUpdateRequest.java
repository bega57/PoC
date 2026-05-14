package at.fhv.blueroute.player.client.dto;

public record BalanceUpdateRequest(
        Double amount,
        String reason
) {
}