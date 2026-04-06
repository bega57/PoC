package at.fhv.blueroute.port.domain.repository;

import at.fhv.blueroute.port.domain.model.Port;

import java.util.Optional;

public interface PortRepository {

    Optional<Port> findById(Long id);

    Optional<Port> findByName(String name);
}