package at.fhv.blueroute.session.application.exception;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(String sessionCode) {
        super("Session with code " + sessionCode + " was not found");
    }

    public SessionNotFoundException(Long id) {
        super("Session with id " + id + " was not found");
    }
}