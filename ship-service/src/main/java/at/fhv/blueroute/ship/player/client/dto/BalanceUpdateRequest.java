package at.fhv.blueroute.ship.player.client.dto;

public record BalanceUpdateRequest(
        Double amount,
        String reason
) {
}