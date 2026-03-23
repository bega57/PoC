package at.fhv.blueroute.session.application.exception;

public class SessionFullException extends RuntimeException {

    public SessionFullException(String sessionCode) {
        super("Session " + sessionCode + " is already full");
    }
}