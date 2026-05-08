package at.fhv.blueroute.player.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public class SelectPortRequest {

    @NotBlank
    private String port;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}