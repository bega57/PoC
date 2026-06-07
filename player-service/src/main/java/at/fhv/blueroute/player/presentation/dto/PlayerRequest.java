package at.fhv.blueroute.player.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlayerRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 2, max = 32, message = "Username must be between 2 and 32 characters")
    private String username;

    @Size(max = 64, message = "Company name must not exceed 64 characters")
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