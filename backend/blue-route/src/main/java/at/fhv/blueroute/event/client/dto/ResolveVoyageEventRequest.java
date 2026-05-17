package at.fhv.blueroute.event.client.dto;

import jakarta.validation.constraints.NotNull;

public class ResolveVoyageEventRequest {

    @NotNull(message = "Selected option is required")
    private String selectedOption;

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
}