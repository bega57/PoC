package at.fhv.blueroute.session.application.mapper;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.presentation.dto.PlayerSummaryResponse;
import at.fhv.blueroute.session.presentation.dto.SessionResponse;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.player.client.dto.PlayerResponse;

import org.springframework.stereotype.Component;


@Component
public class SessionMapper {

    private final PlayerServiceClient playerServiceClient;
    private final ShipServiceClient shipServiceClient;

    public SessionMapper(
            PlayerServiceClient playerServiceClient, ShipServiceClient shipServiceClient) {

        this.playerServiceClient = playerServiceClient;
        this.shipServiceClient = shipServiceClient;
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
                shipServiceClient.getShipsByPlayer(player.getId()),
                player.getCurrentPort()
        );
    }
}