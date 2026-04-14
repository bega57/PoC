package at.fhv.blueroute.common.websocket;

public class SessionUpdateMessage {

    private String type;
    private String sessionCode;
    private int currentTick;

    public SessionUpdateMessage() {
    }

    public SessionUpdateMessage(String type, String sessionCode, int currentTick) {
        this.type = type;
        this.sessionCode = sessionCode;
        this.currentTick = currentTick;
    }

    public String getType() {
        return type;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }
}