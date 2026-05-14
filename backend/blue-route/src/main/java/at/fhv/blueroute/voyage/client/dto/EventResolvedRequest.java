package at.fhv.blueroute.voyage.client.dto;

public class EventResolvedRequest {

    private String resultMessage;

    public EventResolvedRequest() {
    }

    public EventResolvedRequest(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getResultMessage() {
        return resultMessage;
    }
}