package at.fhv.blueroute.ship.infrastructure.persistence;

import at.fhv.blueroute.ship.domain.model.Good;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodRepository extends JpaRepository<Good, Long> {
}