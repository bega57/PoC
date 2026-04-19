package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.common.websocket.SessionUpdateMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.voyage.application.service.FinishVoyageService;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import at.fhv.blueroute.common.websocket.VoyageFinishedMessage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessionTickService {

    private final JpaSessionRepository sessionRepository;
    private final JpaVoyageRepository voyageRepository;
    private final JpaShipRepository shipRepository;
    private final WebSocketSender webSocketSender;
    private final FinishVoyageService finishVoyageService;

    public SessionTickService(JpaSessionRepository sessionRepository,
                              JpaVoyageRepository voyageRepository,
                              JpaShipRepository shipRepository,
                              WebSocketSender webSocketSender,
                              FinishVoyageService finishVoyageService) {
        this.sessionRepository = sessionRepository;
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.webSocketSender = webSocketSender;
        this.finishVoyageService = finishVoyageService;
    }

    public void processTicks() {
        List<Session> runningSessions = sessionRepository.findByStatus(SessionStatus.RUNNING);

        for (Session session : runningSessions) {

            System.out.println("Tick läuft...");
            System.out.println("Current Tick: " + session.getCurrentTick());

            session.setCurrentTick(session.getCurrentTick() + 1);

            List<Voyage> voyages = voyageRepository.findBySessionId(session.getId());

            for (Voyage v : voyages) {
                System.out.println(
                        "Voyage " + v.getId() +
                                " | CurrentTick: " + session.getCurrentTick() +
                                " | ArrivalTick: " + v.getArrivalTick()
                );

                if (v.getStatus() == VoyageStatus.FINISHED) continue;

                if (session.getCurrentTick() >= v.getArrivalTick()) {

                    finishVoyageService.finishVoyage(v.getId());
                    var updatedShip = shipRepository.findById(v.getShipId()).orElseThrow();
                    System.out.println("Fuel AFTER finish: " + updatedShip.getFuelLevel());
                    System.out.println("Voyage finished for ship " + v.getShipId());

                    webSocketSender.sendSessionUpdate(
                            session.getSessionCode(),
                            new VoyageFinishedMessage(
                                    "VOYAGE_FINISHED",
                                    session.getSessionCode(),
                                    v.getId(),
                                    v.getShipId(),
                                    v.getDestinationPort(),
                                    v.getReward()
                            )
                    );
                }
            }

            webSocketSender.sendSessionUpdate(
                    session.getSessionCode(),
                    new SessionUpdateMessage(
                            "TICK",
                            session.getSessionCode(),
                            session.getCurrentTick()
                    )
            );
        }
    }

}