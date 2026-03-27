package at.fhv.blueroute.voyage.application.service;

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

    public StartVoyageService(JpaVoyageRepository voyageRepository,
                              JpaShipRepository shipRepository) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
    }

    public Voyage startVoyage(Long shipId, String origin, String destination) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime arrival = now.plusSeconds(20);

        Voyage voyage = new Voyage();
        voyage.setShipId(shipId);
        voyage.setOriginPort(origin);
        voyage.setDestinationPort(destination);
        voyage.setStatus(VoyageStatus.RUNNING);

        voyage.setStartTime(now);
        voyage.setArrivalTime(arrival);

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        if (ship.isTraveling()) {
            throw new RuntimeException("Ship is already traveling");
        }

        ship.setTraveling(true);

        shipRepository.save(ship);


        return voyageRepository.save(voyage);



    }
}