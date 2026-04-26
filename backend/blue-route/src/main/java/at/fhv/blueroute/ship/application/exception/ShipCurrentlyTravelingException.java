package at.fhv.blueroute.ship.application.exception;

public class ShipCurrentlyTravelingException extends RuntimeException {

    public ShipCurrentlyTravelingException(String shipName) {
        super("Ship with name " + shipName + " is currently traveling and cannot be sold.");
    }
}