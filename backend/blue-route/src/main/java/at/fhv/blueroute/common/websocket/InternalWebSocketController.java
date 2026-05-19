package at.fhv.blueroute.common.websocket;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/session-events")
public class InternalWebSocketController {

    private final WebSocketSender webSocketSender;

    public InternalWebSocketController(WebSocketSender webSocketSender) {
        this.webSocketSender = webSocketSender;
    }

    @PostMapping("/{sessionCode}")
    public ResponseEntity<Void> publishSessionEvent(
            @PathVariable String sessionCode,
            @RequestBody Object payload) {

        webSocketSender.sendSessionUpdate(sessionCode, payload);
        return ResponseEntity.noContent().build();
    }
}