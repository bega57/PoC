package at.fhv.blueroute.ship.application.exception;

public class InvalidFuelAmountException
        extends RuntimeException {

  public InvalidFuelAmountException() {
    super("Invalid fuel amount");
  }
}