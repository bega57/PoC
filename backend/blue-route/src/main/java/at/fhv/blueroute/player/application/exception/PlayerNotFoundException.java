package at.fhv.blueroute.player.application.exception;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(Long id) {
        super("Player with id " + id + " was not found");
    }
}