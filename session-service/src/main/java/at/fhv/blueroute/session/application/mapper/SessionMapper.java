package at.fhv.blueroute.session.application.mapper;

import at.fhv.blueroute.session.player.client.PlayerServiceClient;
import at.fhv.blueroute.session.player.client.dto.PlayerResponse;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.session.presentation.dto.PlayerSummaryResponse;
import at.fhv.blueroute.session.presentation.dto.SessionResponse;
import org.springframework.stereotype.Component;


@Component
public class SessionMapper {

    private final PlayerServiceClient playerServiceClient;

    public SessionMapper(
            PlayerServiceClient playerServiceClient) {
        this.playerServiceClient = playerServiceClient;
    }

    public SessionResponse toResponse(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getSessionCode(),
                session.getStatus().name(),
                session.getCurrentTick(),
                session.getMaxPlayers(),
                session.getCheapShipStock(),
                session.getMediumShipStock(),
                session.getExpensiveShipStock(),
                session.getSessionPlayers().stream()
                        .map(this::toPlayerSummary)
                        .toList()
        );
    }

    private PlayerSummaryResponse toPlayerSummary(SessionPlayer sessionPlayer) {

        PlayerResponse player = playerServiceClient.getPlayer(sessionPlayer.getPlayerId());

        return new PlayerSummaryResponse(
                player.getId(),
                player.getUsername(),
                player.getCompanyName(),
                player.getBalance(),
                sessionPlayer.getStatus().name(),
                player.getCurrentPort()
        );
    }
}