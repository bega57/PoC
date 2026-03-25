package at.fhv.blueroute.ship.domain.repository;

import at.fhv.blueroute.ship.domain.model.Ship;

import java.util.List;

public interface ShipRepository {
    Ship save(Ship ship);
    List<Ship> findByOwnerId(Long ownerId);
}