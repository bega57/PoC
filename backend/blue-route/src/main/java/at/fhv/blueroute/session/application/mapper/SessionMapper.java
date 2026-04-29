package at.fhv.blueroute.session.application.mapper;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.presentation.dto.PlayerSummaryResponse;
import at.fhv.blueroute.session.presentation.dto.SessionResponse;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.ship.application.service.CalculateShipSellPriceService;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    private final ShipMapper shipMapper;
    private final CalculateShipSellPriceService sellPriceService;

    public SessionMapper(ShipMapper shipMapper, CalculateShipSellPriceService sellPriceService) {
        this.shipMapper = shipMapper;
        this.sellPriceService = sellPriceService;
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
        Player player = sessionPlayer.getPlayer();

        return new PlayerSummaryResponse(
                player.getId(),
                player.getUsername(),
                player.getCompanyName(),
                player.getBalance(),
                sessionPlayer.getStatus().name(),
                player.getShips().stream()
                        .map(ship -> shipMapper.toResponse(
                                ship,
                                sellPriceService.calculate(ship),
                                0
                        ))
                        .toList(),
                player.getCurrentPort()

        );
    }
}