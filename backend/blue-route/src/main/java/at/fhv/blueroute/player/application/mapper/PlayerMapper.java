package at.fhv.blueroute.player.application.mapper;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.presentation.dto.PlayerRequest;
import at.fhv.blueroute.player.presentation.dto.PlayerResponse;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toEntity(PlayerRequest request) {
        return new Player(request.getUsername());
    }

    public PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getUsername(),
                player.getCompanyName(),
                player.getBalance());
    }
}