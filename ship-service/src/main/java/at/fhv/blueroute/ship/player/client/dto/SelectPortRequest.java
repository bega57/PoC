package at.fhv.blueroute.ship.player.client.dto;

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