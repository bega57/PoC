package at.fhv.blueroute.ship.application.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long playerId) {
        super("You do not have enough balance to buy this ship.");
    }
}