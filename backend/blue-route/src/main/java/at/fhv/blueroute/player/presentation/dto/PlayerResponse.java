package at.fhv.blueroute.player.presentation.dto;

public class PlayerResponse {

    private Long id;
    private String username;

    public PlayerResponse() {
    }

    public PlayerResponse(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}