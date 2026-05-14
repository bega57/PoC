package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.common.websocket.LeaderboardMessage;
import at.fhv.blueroute.common.websocket.SessionUpdateMessage;
import at.fhv.blueroute.common.websocket.VoyageFinishedMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.session.presentation.dto.LeaderboardEntryResponse;
import at.fhv.blueroute.travel.client.TravelServiceClient;
import at.fhv.blueroute.travel.client.dto.VoyageResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SessionTickService {

    private final JpaSessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;
    private final GetLeaderboardService leaderboardService;
    private final TravelServiceClient travelServiceClient;

    public SessionTickService(
            JpaSessionRepository sessionRepository,
            WebSocketSender webSocketSender,
            GetLeaderboardService leaderboardService,
            TravelServiceClient travelServiceClient
    ) {
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
        this.leaderboardService = leaderboardService;
        this.travelServiceClient = travelServiceClient;
    }

    public void processTicks() {

        System.out.println("PROCESS TICKS RUNNING");

        List<Session> runningSessions =
                sessionRepository.findByStatus(SessionStatus.RUNNING);

        travelServiceClient.processTick();

        for (Session session : runningSessions) {

            session.setCurrentTick(session.getCurrentTick() + 1);

            travelServiceClient.processTick();

            List<VoyageResponse> voyages =
                    travelServiceClient.getVoyages(
                            session.getId(),
                            session.getCurrentTick()
                    );

            for (VoyageResponse v : voyages) {

                if ("FINISHED".equals(v.status)) {
                    continue;
                }

                if (session.getStatus() == SessionStatus.PAUSED) {
                    break;
                }

                if (session.getCurrentTick() >= v.arrivalTick) {

                    System.out.println("FINISHING VOYAGE: " + v.id);

                    travelServiceClient.finishVoyage(
                            v.id,
                            session.getCurrentTick()
                    );

                    webSocketSender.sendSessionUpdate(
                            session.getSessionCode(),
                            new VoyageFinishedMessage(
                                    "VOYAGE_FINISHED",
                                    session.getSessionCode(),
                                    v.id,
                                    v.shipId,
                                    v.destinationPort,
                                    v.reward,
                                    v.eventResultMessage,
                                    v.extraDelayTicks,
                                    v.extraFuelLoss,
                                    v.extraConditionLoss,
                                    v.eventCost,
                                    v.rewardLossPercent
                            )
                    );

                    continue;
                }

                if ("RUNNING".equals(v.status)) {

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
