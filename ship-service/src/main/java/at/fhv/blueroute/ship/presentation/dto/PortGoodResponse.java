package at.fhv.blueroute.ship.presentation.dto;

public record PortGoodResponse(
        Long goodId,
        String name,
        double weight,
        double buyPrice,
        double sellPrice,
        int stock
) {}