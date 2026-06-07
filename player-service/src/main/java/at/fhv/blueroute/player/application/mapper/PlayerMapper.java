package at.fhv.blueroute.player.application.mapper;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.presentation.dto.PlayerRequest;
import at.fhv.blueroute.player.presentation.dto.PlayerResponse;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toEntity(PlayerRequest request) {
        Player player = new Player(request.getUsername());
        player.setCompanyName(request.getCompanyName());
        return player;
    }

    public PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getUsername(),
                player.getCompanyName(),
                player.getBalance(),
                player.getCurrentPort(),
                player.getPoints()
        );
    }
}