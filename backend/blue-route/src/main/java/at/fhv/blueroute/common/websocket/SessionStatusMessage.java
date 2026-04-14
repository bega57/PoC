package at.fhv.blueroute.common.websocket;

public class SessionStatusMessage {

    private String type;
    private String sessionCode;
    private String status;

    public SessionStatusMessage() {
    }

    public SessionStatusMessage(String type, String sessionCode, String status) {
        this.type = type;
        this.sessionCode = sessionCode;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public String getStatus() {
        return status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}