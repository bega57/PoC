package at.fhv.blueroute.voyage.infrastructure.persistence;

import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaVoyageRepository extends JpaRepository<Voyage, Long> {
    boolean existsByShipIdAndStatusNot(Long shipId, VoyageStatus status);
}