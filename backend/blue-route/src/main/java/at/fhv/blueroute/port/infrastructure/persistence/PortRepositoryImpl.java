package at.fhv.blueroute.port.infrastructure.persistence;

import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.domain.repository.PortRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PortRepositoryImpl implements PortRepository {

    private final JpaPortRepository jpaPortRepository;

    public PortRepositoryImpl(JpaPortRepository jpaPortRepository) {
        this.jpaPortRepository = jpaPortRepository;
    }

    @Override
    public Optional<Port> findById(Long id) {
        return jpaPortRepository.findById(id);
    }

    @Override
    public Optional<Port> findByName(String name) {
        return jpaPortRepository.findByName(name);
    }
}