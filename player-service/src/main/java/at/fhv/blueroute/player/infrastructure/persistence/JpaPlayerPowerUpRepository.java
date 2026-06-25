package at.fhv.blueroute.player.infrastructure.persistence;

import at.fhv.blueroute.player.domain.model.PlayerPowerUp;
import at.fhv.blueroute.player.domain.model.PowerUpType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaPlayerPowerUpRepository extends JpaRepository<PlayerPowerUp, Long> {

    List<PlayerPowerUp> findByPlayerId(Long playerId);

    Optional<PlayerPowerUp> findByPlayerIdAndPowerUpType(Long playerId, PowerUpType powerUpType);
}
