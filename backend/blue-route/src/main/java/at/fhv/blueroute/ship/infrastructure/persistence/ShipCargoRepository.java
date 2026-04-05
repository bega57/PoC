package at.fhv.blueroute.ship.infrastructure.persistence;

import at.fhv.blueroute.ship.domain.model.ShipCargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipCargoRepository extends JpaRepository<ShipCargo, Long> {

    Optional<ShipCargo> findByShipIdAndGoodId(Long shipId, Long goodId);

    List<ShipCargo> findByShipId(Long shipId);
}