package at.fhv.blueroute.port.infrastructure.persistence;

import at.fhv.blueroute.port.domain.model.Port;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPortRepository extends JpaRepository<Port, Long> {

    Optional<Port> findByName(String name);
}