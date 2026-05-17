package at.fhv.blueroute.event.voyage.client.dto;

public class EventResolvedRequest {

    private String resultMessage;

    public String getResultMessage() {
        return resultMessage;
    }

    public EventResolvedRequest(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}