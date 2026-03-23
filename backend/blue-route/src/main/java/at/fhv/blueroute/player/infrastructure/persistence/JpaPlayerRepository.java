package at.fhv.blueroute.player.infrastructure.persistence;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPlayerRepository extends JpaRepository<Player, Long>, PlayerRepository {
}