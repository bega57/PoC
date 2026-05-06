package at.fhv.blueroute.websocket;

public class WebSocketEvent {

    private String type;

    public WebSocketEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}