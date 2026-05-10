package at.fhv.blueroute.ship.application.exception;

public class InvalidRepairAmountException
        extends RuntimeException {

    public InvalidRepairAmountException() {
        super("Invalid repair amount");
    }
}