package at.fhv.blueroute.player.application.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Long playerId) {
        super("Player " + playerId + " has insufficient balance.");
    }
}