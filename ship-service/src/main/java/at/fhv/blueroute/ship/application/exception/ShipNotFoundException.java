package at.fhv.blueroute.ship.application.exception;

public class ShipNotFoundException
        extends RuntimeException {

  public ShipNotFoundException(Long shipId) {
    super("Ship not found with id: " + shipId);
  }
}