package at.fhv.blueroute.event.session.client.dto;

public class SessionStatusUpdateRequest {

    private String status;

    public SessionStatusUpdateRequest() {}
    public SessionStatusUpdateRequest(String status) { this.status = status; }
    public String getStatus() { return status; }
}