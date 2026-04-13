package at.fhv.blueroute.common.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketSender {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendSessionUpdate(String sessionCode, Object payload) {
        messagingTemplate.convertAndSend("/topic/session/" + sessionCode, payload);
    }
}