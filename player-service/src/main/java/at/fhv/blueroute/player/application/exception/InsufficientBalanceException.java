package at.fhv.blueroute.player.application.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long playerId) {
        super("You don't have enough balance for this.");
    }
}