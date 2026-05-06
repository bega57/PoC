package at.fhv.blueroute.event.presentation.dto;

public class ResolveVoyageEventResponse {

    private String message;

    public ResolveVoyageEventResponse() {
    }

    public ResolveVoyageEventResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}