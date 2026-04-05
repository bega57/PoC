package at.fhv.blueroute.ship.infrastructure.persistence;

import at.fhv.blueroute.ship.domain.model.PortGood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortGoodRepository extends JpaRepository<PortGood, Long> {
    List<PortGood> findByPortId(Long portId);

    Optional<PortGood> findByPortIdAndGoodId(Long portId, Long goodId);
}