package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.player.application.service.CalculatePlayerScoreService;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.repository.SessionRepository;
import at.fhv.blueroute.session.presentation.dto.LeaderboardEntryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetLeaderboardService {

    private final SessionRepository sessionRepository;
    private final CalculatePlayerScoreService scoreService;

    public GetLeaderboardService(SessionRepository sessionRepository,
                                 CalculatePlayerScoreService scoreService) {
        this.sessionRepository = sessionRepository;
        this.scoreService = scoreService;
    }

    public List<LeaderboardEntryResponse> getLeaderboard(String sessionCode) {

        Session session = sessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        return session.getSessionPlayers().stream()
                .map(sp -> {
                    Player player = sp.getPlayer();

                    int score = scoreService.calculateScore(player);

                    return new LeaderboardEntryResponse(
                            player.getId(),
                            player.getUsername(),
                            score
                    );
                })
                .sorted((a, b) -> b.getScore() - a.getScore())
                .toList();
    }
}
