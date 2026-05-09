package at.fhv.blueroute.ship.infrastructure.persistence;

import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ShipRepositoryImpl implements ShipRepository {

    private final JpaShipRepository jpaRepository;

    public ShipRepositoryImpl(JpaShipRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Ship> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Ship save(Ship ship) {
        return jpaRepository.save(ship);
    }

    @Override
    public List<Ship> findByOwnerId(Long ownerId) {
        return jpaRepository.findByOwnerId(ownerId);
    }
}