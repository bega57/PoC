package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VoyageStatusService {

    private final JpaShipRepository jpaShipRepository;

    public VoyageStatusService(JpaShipRepository jpaShipRepository) {
        this.jpaShipRepository = jpaShipRepository;
    }


    public void updateStatus(Voyage voyage) {
        if (voyage.getStatus() == VoyageStatus.RUNNING &&
                voyage.getArrivalTime() != null &&
                voyage.getArrivalTime().isBefore(LocalDateTime.now())) {

            voyage.setStatus(VoyageStatus.FINISHED);

            Ship ship = jpaShipRepository.findById(voyage.getShipId())
                    .orElseThrow(() -> new RuntimeException("Ship not found"));

            ship.setTraveling(false);
            ship.setCurrentPort(voyage.getDestinationPort());

            jpaShipRepository.save(ship);
        }
    }
}