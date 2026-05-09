package at.fhv.blueroute.player.infrastructure.persistence;

import at.fhv.blueroute.player.domain.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPlayerRepository extends JpaRepository<Player, Long> {
}