package at.fhv.blueroute.player.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCompanyNameRequest {

    @NotBlank(message = "Company name must not be blank")
    @Size(max = 64, message = "Company name must not exceed 64 characters")
    private String companyName;

    public UpdateCompanyNameRequest() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}