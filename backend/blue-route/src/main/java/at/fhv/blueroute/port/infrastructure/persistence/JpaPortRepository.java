package at.fhv.blueroute.port.infrastructure.persistence;

import at.fhv.blueroute.port.domain.model.Port;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPortRepository extends JpaRepository<Port, String> {
}