package at.fhv.blueroute.player.application.mapper;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.presentation.dto.PlayerRequest;
import at.fhv.blueroute.player.presentation.dto.PlayerResponse;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerMapper {

    private final ShipMapper shipMapper;

    public PlayerMapper(ShipMapper shipMapper) {
        this.shipMapper = shipMapper;
    }

    public Player toEntity(PlayerRequest request) {
        return new Player(request.getUsername());
    }

    public PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getUsername(),
                player.getCompanyName(),
                player.getBalance(),
                (player.getShips() == null ? List.<at.fhv.blueroute.ship.domain.model.Ship>of() : player.getShips())
                        .stream()
                        .map(shipMapper::toResponse)
                        .toList(),
                player.getCurrentPort()
        );
    }
}