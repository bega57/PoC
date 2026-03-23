package at.fhv.blueroute.player.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public class PlayerRequest {

    @NotBlank(message = "Username must not be blank")
    private String username;

    public PlayerRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}