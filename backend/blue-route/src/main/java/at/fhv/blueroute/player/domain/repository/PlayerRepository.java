package at.fhv.blueroute.player.domain.repository;

import at.fhv.blueroute.player.domain.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    List<Player> findAll();
    Optional<Player> findById(Long id);
    Player save(Player player);
}
