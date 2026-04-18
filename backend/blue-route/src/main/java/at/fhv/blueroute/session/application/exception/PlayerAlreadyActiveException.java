package at.fhv.blueroute.session.application.exception;

public class PlayerAlreadyActiveException extends RuntimeException {

    public PlayerAlreadyActiveException(Long playerId, String sessionCode) {
        super("Player " + playerId + " is already active in session " + sessionCode + ".");
    }
}