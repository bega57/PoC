package at.fhv.blueroute.cargo.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCargoByPortService {

    private final JpaCargoRepository repo;

    public GetCargoByPortService(JpaCargoRepository repo) {
        this.repo = repo;
    }

    public List<Cargo> execute(String portName) {
        return repo.findByOriginPort_Name(portName);
    }
}