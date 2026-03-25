package at.fhv.blueroute.ship.infrastructure.persistence;

import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaShipRepository extends JpaRepository<Ship, Long>, ShipRepository {
    List<Ship> findByOwnerId(Long ownerId);
}