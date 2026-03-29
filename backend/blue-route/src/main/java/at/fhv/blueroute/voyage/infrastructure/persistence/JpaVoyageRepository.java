package at.fhv.blueroute.voyage.infrastructure.persistence;

import at.fhv.blueroute.voyage.domain.model.Voyage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaVoyageRepository extends JpaRepository<Voyage, Long> {
}