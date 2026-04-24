package at.fhv.blueroute.ship.application.exception;

public class ShipOutOfStockException extends RuntimeException {

    public ShipOutOfStockException(String shipType) {
        super("No " + shipType.toLowerCase() + " ships left.");
    }
}