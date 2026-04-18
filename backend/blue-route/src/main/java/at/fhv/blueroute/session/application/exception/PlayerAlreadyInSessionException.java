package at.fhv.blueroute.session.application.exception;

public class PlayerAlreadyInSessionException extends RuntimeException {

  public PlayerAlreadyInSessionException(Long playerId, String sessionCode) {
    super("Player " + playerId + " already belongs to session " + sessionCode + ".");
  }
}