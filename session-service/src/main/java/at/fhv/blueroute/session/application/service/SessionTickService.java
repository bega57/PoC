package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.event.client.EventServiceClient;
import at.fhv.blueroute.session.websocket.dto.SessionStatusMessage;
import at.fhv.blueroute.session.websocket.dto.SessionUpdateMessage;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.domain.repository.SessionRepository;
import at.fhv.blueroute.session.voyage.client.VoyageServiceClient;
import at.fhv.blueroute.session.voyage.client.dto.VoyageResponse;
import at.fhv.blueroute.session.websocket.dto.VoyageFinishedMessage;
import at.fhv.blueroute.session.backend.client.BackendWebSocketClient;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SessionTickService {

    private final SessionRepository sessionRepository;
    private final VoyageServiceClient voyageServiceClient;
    private final BackendWebSocketClient backendWebSocketClient;
    private final EventServiceClient eventServiceClient;

    public SessionTickService(
            SessionRepository sessionRepository,
            VoyageServiceClient voyageServiceClient,
            BackendWebSocketClient backendWebSocketClient,
            EventServiceClient eventServiceClient
    ) {
        this.sessionRepository = sessionRepository;
        this.voyageServiceClient = voyageServiceClient;
        this.backendWebSocketClient = backendWebSocketClient;
        this.eventServiceClient = eventServiceClient;
    }

    public void processTicks() {
        List<Session> runningSessions = sessionRepository.findByStatus(SessionStatus.RUNNING);

        for (Session session : runningSessions) {
            try {
                session.setCurrentTick(session.getCurrentTick() + 1);

                // 1. ZUERST Events prüfen
                boolean anyEventTriggered = eventServiceClient.processTick(
                        session.getId(),
                        session.getSessionCode(),
                        session.getCurrentTick()
                );

                if (anyEventTriggered) {
                    session.setPausedByEvent(true);
                    session.setStatus(SessionStatus.PAUSED);
                    sessionRepository.save(session);
                    backendWebSocketClient.publish(
                            session.getSessionCode(),
                            new SessionStatusMessage(
                                    "SESSION_PAUSED",
                                    session.getSessionCode(),
                                    "PAUSED"
                            )
                    );
                    continue;
                }

                // 2. NUR wenn kein Event → Voyage weiterführen
                List<VoyageResponse> finishedVoyages =
                        voyageServiceClient.processTick(
                                session.getId(),
                                session.getCurrentTick()
                        );

                for (VoyageResponse finishedVoyage : finishedVoyages) {
                    backendWebSocketClient.publish(
                            session.getSessionCode(),
                            new VoyageFinishedMessage(
                                    finishedVoyage.getId(),
                                    finishedVoyage.getShipId(),
                                    finishedVoyage.getShipName(),
                                    finishedVoyage.getOriginPort(),
                                    finishedVoyage.getDestinationPort(),
                                    finishedVoyage.getReward(),
                                    finishedVoyage.isSmuggling(),
                                    finishedVoyage.getSmugglingReward(),
                                    finishedVoyage.isCustomsChecked(),
                                    finishedVoyage.isSmugglingDetected(),
                                    finishedVoyage.getSmugglingPenalty(),
                                    finishedVoyage.getSmugglingDetentionTicks(),
                                    finishedVoyage.isSmugglingResolved()
                            )
                    );
                }

                sessionRepository.save(session);

                backendWebSocketClient.publish(
                        session.getSessionCode(),
                        new SessionUpdateMessage(
                                "TICK",
                                session.getSessionCode(),
                                session.getCurrentTick()
                        )
                );
            } catch (Exception e) {
                System.err.println("Error processing tick for session "
                        + session.getSessionCode() + ": " + e.getMessage());
            }
        }
    }
}
