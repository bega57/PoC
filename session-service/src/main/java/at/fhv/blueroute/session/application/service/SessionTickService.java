package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.common.websocket.SessionUpdateMessage;
import at.fhv.blueroute.session.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.domain.repository.SessionRepository;
import at.fhv.blueroute.session.voyage.client.VoyageServiceClient;
import at.fhv.blueroute.session.voyage.client.dto.VoyageResponse;
import at.fhv.blueroute.session.common.websocket.VoyageFinishedMessage;
import at.fhv.blueroute.session.backend.client.BackendWebSocketClient;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SessionTickService {

    private final SessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;
    private final VoyageServiceClient voyageServiceClient;
    private final BackendWebSocketClient backendWebSocketClient;

    public SessionTickService(
            SessionRepository sessionRepository,
            WebSocketSender webSocketSender,
            VoyageServiceClient voyageServiceClient,
            BackendWebSocketClient backendWebSocketClient
    ) {
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
        this.voyageServiceClient = voyageServiceClient;
        this.backendWebSocketClient = backendWebSocketClient;
    }

    public void processTicks() {
        List<Session> runningSessions = sessionRepository.findByStatus(SessionStatus.RUNNING);

        for (Session session : runningSessions) {
            session.setCurrentTick(session.getCurrentTick() + 1);
            List<VoyageResponse> finishedVoyages =
                    voyageServiceClient.processTick(
                            session.getId(),
                            session.getCurrentTick()
                    );
            for (VoyageResponse finishedVoyage : finishedVoyages) {

                backendWebSocketClient.publish(
                        session.getSessionCode(),
                        new VoyageFinishedMessage(
                                "VOYAGE_FINISHED",
                                session.getSessionCode(),
                                finishedVoyage.getId(),
                                finishedVoyage.getShipId(),
                                finishedVoyage.getDestinationPort(),
                                finishedVoyage.getReward(),
                                finishedVoyage.getEventResultMessage(),
                                finishedVoyage.getExtraDelayTicks(),
                                finishedVoyage.getExtraFuelLoss(),
                                finishedVoyage.getExtraConditionLoss(),
                                finishedVoyage.getEventCost(),
                                finishedVoyage.getRewardLossPercent()
                        )
                );
            }
            sessionRepository.save(session);

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