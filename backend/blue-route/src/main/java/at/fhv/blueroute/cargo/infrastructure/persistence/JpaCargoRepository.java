package at.fhv.blueroute.cargo.infrastructure.persistence;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCargoRepository extends JpaRepository<Cargo, Long> {

    List<Cargo> findByOriginPort_Name(String portName);
}