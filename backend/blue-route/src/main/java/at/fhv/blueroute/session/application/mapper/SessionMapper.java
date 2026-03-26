package at.fhv.blueroute.session.application.mapper;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.presentation.dto.PlayerSummaryResponse;
import at.fhv.blueroute.session.presentation.dto.SessionResponse;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    private final ShipMapper shipMapper;

    public SessionMapper(ShipMapper shipMapper) {
        this.shipMapper = shipMapper;
    }

    public SessionResponse toResponse(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getSessionCode(),
                session.getStatus().name(),
                session.getCurrentTick(),
                session.getMaxPlayers(),
                session.getSessionPlayers().stream()
                        .map(this::toPlayerSummary)
                        .toList()
        );
    }

    private PlayerSummaryResponse toPlayerSummary(SessionPlayer sessionPlayer) {
        Player player = sessionPlayer.getPlayer();

        return new PlayerSummaryResponse(
                player.getId(),
                player.getUsername(),
                player.getCompanyName(),
                player.getBalance(),
                sessionPlayer.getStatus().name(),
                player.getShips().stream()
                        .map(shipMapper::toResponse)
                        .toList()
        );
    }
}