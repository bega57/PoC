package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.common.websocket.LeaderboardMessage;
import at.fhv.blueroute.common.websocket.SessionUpdateMessage;
import at.fhv.blueroute.common.websocket.VoyageFinishedMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.session.presentation.dto.LeaderboardEntryResponse;
import at.fhv.blueroute.voyage.client.VoyageServiceClient;
import at.fhv.blueroute.voyage.client.dto.VoyageResponse;
import at.fhv.blueroute.event.application.service.VoyageEventTriggerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SessionTickService {

    private final JpaSessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;
    private final GetLeaderboardService leaderboardService;
    private final VoyageServiceClient voyageServiceClient;
    private final VoyageEventTriggerService voyageEventTriggerService;

    public SessionTickService(
            JpaSessionRepository sessionRepository,
            WebSocketSender webSocketSender,
            GetLeaderboardService leaderboardService,
            VoyageServiceClient voyageServiceClient,
            VoyageEventTriggerService voyageEventTriggerService
    ) {
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
        this.leaderboardService = leaderboardService;
        this.voyageServiceClient = voyageServiceClient;
        this.voyageEventTriggerService = voyageEventTriggerService;
    }

    public void processTicks() {

        List<Session> runningSessions =
                sessionRepository.findByStatus(SessionStatus.RUNNING);

        for (Session session : runningSessions) {

            session.setCurrentTick(session.getCurrentTick() + 1);

            List<VoyageResponse> finishedVoyages =
                    voyageServiceClient.processTick(
                            session.getId(),
                            session.getCurrentTick()
                    );

            List<VoyageResponse> activeVoyages =
                    voyageServiceClient.getVoyages(
                            session.getId(),
                            session.getCurrentTick()
                    );

            for (VoyageResponse voyage : activeVoyages) {
                voyageEventTriggerService.triggerEventIfNeeded(voyage, session);
            }

            for (VoyageResponse finishedVoyage : finishedVoyages) {

                webSocketSender.sendSessionUpdate(
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

            webSocketSender.sendSessionUpdate(
                    session.getSessionCode(),
                    new SessionUpdateMessage(
                            "TICK",
                            session.getSessionCode(),
                            session.getCurrentTick()
                    )
            );

            List<LeaderboardEntryResponse> leaderboard =
                    leaderboardService.getLeaderboard(session.getSessionCode());

            webSocketSender.sendSessionUpdate(
                    session.getSessionCode(),
                    new LeaderboardMessage(
                            "LEADERBOARD_UPDATE",
                            session.getSessionCode(),
                            leaderboard
                    )
            );
        }
    }
}
