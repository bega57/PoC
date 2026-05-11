package at.fhv.blueroute.ship.application.exception;

public class RepairLimitExceededException
        extends RuntimeException {

  public RepairLimitExceededException() {
    super("Repair amount exceeds max condition");
  }
}