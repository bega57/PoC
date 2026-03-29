package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StartVoyageService {

    private final JpaVoyageRepository voyageRepository;
    private final JpaShipRepository shipRepository;
    private final JpaCargoRepository cargoRepository;

    public StartVoyageService(
            JpaVoyageRepository voyageRepository,
            JpaShipRepository shipRepository,
            JpaCargoRepository cargoRepository
    ) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.cargoRepository = cargoRepository;
    }

    public Voyage startVoyage(Long shipId, Long cargoId) {
        try {

            Cargo cargo = cargoRepository.findById(cargoId)
                    .orElseThrow(() -> new RuntimeException("Cargo not found"));

            Ship ship = shipRepository.findById(shipId)
                    .orElseThrow(() -> new RuntimeException("Ship not found"));

            if (ship.isTraveling()) {
                throw new RuntimeException("Ship is already traveling");
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime arrival = now.plusSeconds(20);

            Voyage voyage = new Voyage();
            voyage.setShipId(shipId);
            voyage.setOriginPort(cargo.getOriginPort().getName());
            voyage.setDestinationPort(cargo.getDestinationPort().getName());
            voyage.setStatus(VoyageStatus.RUNNING);
            voyage.setStartTime(now);
            voyage.setArrivalTime(arrival);

            ship.setTraveling(true);
            ship.setCurrentPort(null);
            shipRepository.save(ship);

            return voyageRepository.save(voyage);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}