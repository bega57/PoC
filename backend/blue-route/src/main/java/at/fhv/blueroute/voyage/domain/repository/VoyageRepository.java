package at.fhv.blueroute.voyage.domain.repository;

import at.fhv.blueroute.voyage.domain.model.Voyage;

import java.util.Optional;

public interface VoyageRepository {

    Voyage save(Voyage voyage);

    Optional<Voyage> findById(Long id);
}