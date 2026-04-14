package at.fhv.blueroute.session.application.exception;

public class SessionPlayerNotFoundException extends RuntimeException {

    public SessionPlayerNotFoundException(Long playerId) {
        super("Player with id " + playerId + " is not part of this session.");
    }

}