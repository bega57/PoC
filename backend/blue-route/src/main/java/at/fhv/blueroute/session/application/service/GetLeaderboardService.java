package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.session.domain.repository.SessionRepository;
import at.fhv.blueroute.session.presentation.dto.LeaderboardEntryResponse;
import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.player.client.dto.PlayerResponse;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GetLeaderboardService {

    private final SessionRepository sessionRepository;
    private final PlayerServiceClient playerServiceClient;

    public GetLeaderboardService(SessionRepository sessionRepository,
                                 PlayerServiceClient playerServiceClient) {
        this.sessionRepository = sessionRepository;
        this.playerServiceClient = playerServiceClient;
    }

    public List<LeaderboardEntryResponse> getLeaderboard(String sessionCode) {

        Session session = sessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        return (session.getSessionPlayers() == null
                ? Collections.<SessionPlayer>emptyList()
                : session.getSessionPlayers())
                .stream()
                .map(sp -> {
                    Long playerId = sp.getPlayerId();

                    PlayerResponse player = playerServiceClient.getPlayer(playerId);

                    if (player == null || player.getUsername() == null) return null;

                    int score = player.getBalance() == null ? 0 : player.getBalance().intValue();

                    return new LeaderboardEntryResponse(
                            player.getId(),
                            player.getUsername(),
                            score
                    );
                })
                .filter(e -> e != null)
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .toList();
    }
}
