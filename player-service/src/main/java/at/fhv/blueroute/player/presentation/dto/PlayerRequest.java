package at.fhv.blueroute.player.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public class PlayerRequest {

    @NotBlank(message = "Username must not be blank")
    private String username;

    private String companyName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}