package at.fhv.blueroute.session.presentation.rest;

import at.fhv.blueroute.session.application.service.SessionService;
import at.fhv.blueroute.session.presentation.dto.CreateSessionRequest;
import at.fhv.blueroute.session.presentation.dto.JoinSessionRequest;
import at.fhv.blueroute.session.presentation.dto.SessionResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public List<SessionResponse> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/{sessionCode}")
    public SessionResponse getSessionByCode(@PathVariable String sessionCode) {
        return sessionService.getSessionByCode(sessionCode);
    }

    @PostMapping
    public SessionResponse createSession(@Valid @RequestBody CreateSessionRequest request) {
        return sessionService.createSession(request.getPlayerId(), request.getMaxPlayers());
    }

    @PostMapping("/{sessionCode}/join")
    public SessionResponse joinSession(@PathVariable String sessionCode,
                                       @Valid @RequestBody JoinSessionRequest request) {
        return sessionService.joinSession(sessionCode, request.getPlayerId());
    }

}