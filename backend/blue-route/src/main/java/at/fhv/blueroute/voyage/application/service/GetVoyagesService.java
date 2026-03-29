package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetVoyagesService {

    private final JpaVoyageRepository voyageRepository;
    private final VoyageStatusService voyageStatusService;

    public GetVoyagesService(JpaVoyageRepository voyageRepository,
                             VoyageStatusService voyageStatusService) {
        this.voyageRepository = voyageRepository;
        this.voyageStatusService = voyageStatusService;
    }

    public List<Voyage> getAllVoyages() {
        List<Voyage> voyages = voyageRepository.findAll();

        voyages.forEach(voyageStatusService::updateStatus);

        return voyages;
    }
}