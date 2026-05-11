package at.fhv.blueroute.ship.application.exception;

public class FuelCapacityExceededException
        extends RuntimeException {

  public FuelCapacityExceededException() {
    super("Fuel exceeds tank capacity");
  }
}