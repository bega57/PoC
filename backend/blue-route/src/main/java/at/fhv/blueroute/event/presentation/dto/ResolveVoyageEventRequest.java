package at.fhv.blueroute.event.presentation.dto;

import at.fhv.blueroute.event.domain.model.VoyageEventOption;
import jakarta.validation.constraints.NotNull;

public class ResolveVoyageEventRequest {

    @NotNull(message = "Selected option is required")
    private VoyageEventOption selectedOption;

    public VoyageEventOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(VoyageEventOption selectedOption) {
        this.selectedOption = selectedOption;
    }
}