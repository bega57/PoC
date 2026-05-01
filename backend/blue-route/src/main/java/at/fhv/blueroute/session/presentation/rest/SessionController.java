package at.fhv.blueroute.session.presentation.rest;

import at.fhv.blueroute.session.application.service.GetLeaderboardService;
import at.fhv.blueroute.session.application.service.SessionService;
import at.fhv.blueroute.session.presentation.dto.CreateSessionRequest;
import at.fhv.blueroute.session.presentation.dto.JoinSessionRequest;
import at.fhv.blueroute.session.presentation.dto.LeaderboardEntryResponse;
import at.fhv.blueroute.session.presentation.dto.SessionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final GetLeaderboardService leaderboardService;

    public SessionController(SessionService sessionService, GetLeaderboardService leaderboardService) {
        this.sessionService = sessionService;
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public List<SessionResponse> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/{sessionCode}")
    public SessionResponse getSessionByCode(@PathVariable String sessionCode) {
        return sessionService.getSessionByCode(sessionCode);
    }

    @GetMapping("/{sessionCode}/leaderboard")
    public List<LeaderboardEntryResponse> getLeaderboard(@PathVariable String sessionCode) {
        return leaderboardService.getLeaderboard(sessionCode);
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

    @PostMapping("/{sessionCode}/resume")
    public SessionResponse resumeSession(@PathVariable String sessionCode,
                                         @Valid @RequestBody JoinSessionRequest request) {
        return sessionService.resumeSession(sessionCode, request.getPlayerId());
    }

    @PostMapping("/{sessionCode}/leave")
    public SessionResponse leaveSession(@PathVariable String sessionCode,
                                        @Valid @RequestBody JoinSessionRequest request) {
        return sessionService.leaveSession(sessionCode, request.getPlayerId());
    }

    @PatchMapping("/{sessionCode}/players/{playerId}/heartbeat")
    public ResponseEntity<Void> heartbeat(
            @PathVariable String sessionCode,
            @PathVariable Long playerId) {

        sessionService.heartbeat(sessionCode, playerId);
        return ResponseEntity.noContent().build();
    }

}