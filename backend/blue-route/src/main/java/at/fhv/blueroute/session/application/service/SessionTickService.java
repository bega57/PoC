package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessionTickService {

    private final JpaSessionRepository sessionRepository;
    private final JpaVoyageRepository voyageRepository;
    private final JpaShipRepository shipRepository;

    public SessionTickService(JpaSessionRepository sessionRepository,
                              JpaVoyageRepository voyageRepository,
                              JpaShipRepository shipRepository) {
        this.sessionRepository = sessionRepository;
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
    }

    public void processTicks() {
        List<Session> runningSessions = sessionRepository.findByStatus(SessionStatus.RUNNING);

        for (Session session : runningSessions) {

            System.out.println("Tick läuft...");
            System.out.println("Current Tick: " + session.getCurrentTick());

            session.setCurrentTick(session.getCurrentTick() + 1);

            List<Voyage> voyages = voyageRepository.findBySessionId(session.getId());

            for (Voyage v : voyages) {

                if (v.getStatus() == VoyageStatus.FINISHED) continue;

                if (session.getCurrentTick() >= v.getArrivalTick()) {

                    v.setStatus(VoyageStatus.FINISHED);
                    v.setArrivalTime(LocalDateTime.now());

                    var ship = shipRepository.findById(v.getShipId()).orElseThrow();
                    ship.setTraveling(false);
                    ship.setCurrentPort(v.getDestinationPort());

                    var player = ship.getOwner();
                    player.setBalance(player.getBalance() + v.getReward());

                    v.setRewardGranted(true);

                }
            }
        }
    }

    @Scheduled(fixedRate = 3000)
    public void runTicks() {
        System.out.println("RUN TICK CALLED");
        processTicks();
    }
}