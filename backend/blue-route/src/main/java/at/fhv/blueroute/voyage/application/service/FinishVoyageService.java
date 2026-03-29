package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import org.springframework.stereotype.Service;

@Service
public class FinishVoyageService {

    private final JpaVoyageRepository voyageRepository;
    private final JpaShipRepository shipRepository;

    public FinishVoyageService(JpaVoyageRepository voyageRepository,
                               JpaShipRepository shipRepository) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
    }

    public void finishVoyage(Long voyageId) {

        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow();

        Ship ship = shipRepository.findById(voyage.getShipId())
                .orElseThrow();

        ship.setTraveling(false);
        ship.setCurrentPort(voyage.getDestinationPort());

        voyage.setStatus(VoyageStatus.FINISHED);

        shipRepository.save(ship);
        voyageRepository.save(voyage);
    }
}