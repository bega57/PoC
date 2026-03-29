package at.fhv.blueroute.ship.domain.repository;

import at.fhv.blueroute.ship.domain.model.Ship;

import java.util.List;
import java.util.Optional;

public interface ShipRepository {
    //Ship save(Ship ship);
    List<Ship> findByOwnerId(Long ownerId);

    Optional<Ship> findById(Long id);
    void delete(Ship ship);
}