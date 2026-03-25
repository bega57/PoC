package at.fhv.blueroute.ship.application.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long playerId) {
        super("Player with id " + playerId + " does not have enough money to buy this ship.");
    }
}