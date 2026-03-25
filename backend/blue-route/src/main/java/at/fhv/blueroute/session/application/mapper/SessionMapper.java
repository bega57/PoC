package at.fhv.blueroute.session.application.mapper;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.presentation.dto.PlayerSummaryResponse;
import at.fhv.blueroute.session.presentation.dto.SessionResponse;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public SessionResponse toResponse(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getSessionCode(),
                session.getStatus().name(),
                session.getCurrentTick(),
                session.getMaxPlayers(),
                session.getPlayers().stream()
                        .map(this::toPlayerSummary)
                        .toList()
        );
    }

    private PlayerSummaryResponse toPlayerSummary(Player player) {
        return new PlayerSummaryResponse(
                player.getId(),
                player.getUsername(),
                player.getCompanyName(),
                player.getBalance()
        );
    }
}