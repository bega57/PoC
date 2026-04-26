package at.fhv.blueroute.ship.presentation.dto;

public record UsedShipOfferResponse(
        Long id,
        String type,
        double price,
        int speed,
        int capacity,
        double condition,
        double fuelLevel
) {
}