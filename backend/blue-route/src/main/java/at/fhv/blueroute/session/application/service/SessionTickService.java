package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.cargo.application.service.CalculateDeteriorationService;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.common.websocket.LeaderboardMessage;
import at.fhv.blueroute.common.websocket.SessionUpdateMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.session.presentation.dto.LeaderboardEntryResponse;
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
    private final JpaCargoRepository cargoRepository;
    private final CalculateDeteriorationService deteriorationService;
    private final GetLeaderboardService leaderboardService;

    public SessionTickService(
            JpaSessionRepository sessionRepository,
            JpaVoyageRepository voyageRepository,
            JpaShipRepository shipRepository,
            WebSocketSender webSocketSender,
            FinishVoyageService finishVoyageService,
            JpaCargoRepository cargoRepository,
            CalculateDeteriorationService deteriorationService, GetLeaderboardService leaderboardService
    ) {
        this.sessionRepository = sessionRepository;
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.webSocketSender = webSocketSender;
        this.finishVoyageService = finishVoyageService;
        this.cargoRepository = cargoRepository;
        this.deteriorationService = deteriorationService;
        this.leaderboardService = leaderboardService;
    }

    public void processTicks() {

        List<Session> runningSessions =
                sessionRepository.findByStatus(SessionStatus.RUNNING);

        for (Session session : runningSessions) {

            session.setCurrentTick(session.getCurrentTick() + 1);

            List<Voyage> voyages =
                    voyageRepository.findBySessionId(session.getId());

            for (Voyage v : voyages) {

                if (v.getStatus() == VoyageStatus.FINISHED) continue;

                if (session.getCurrentTick() >= v.getArrivalTick()) {

                    finishVoyageService.finishVoyage(
                            v.getId(),
                            session.getCurrentTick()
                    );

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

                    continue;
                }

                if (v.getStatus() == VoyageStatus.RUNNING) {

                    var ship = shipRepository.findById(v.getShipId()).orElseThrow();
                    var cargo = cargoRepository.findById(v.getCargoId()).orElseThrow();

                    int duration = Math.max(1,
                            v.getArrivalTick() - v.getStartTick()
                    );

                    double shipFuelMultiplier = switch (ship.getType()) {
                        case CHEAP -> 1.25;
                        case MEDIUM -> 1.0;
                        case EXPENSIVE -> 0.8;
                    };

                    double shipConditionMultiplier = switch (ship.getType()) {
                        case CHEAP -> 1.5;
                        case MEDIUM -> 1.0;
                        case EXPENSIVE -> 0.7;
                    };


                    double cargoFuelPerTick = (cargo.getFuelConsumption() / duration) * shipFuelMultiplier;
                    double extraFuelPerTick = 1.0 + (duration * 0.02);
                    double fuelPerTick = cargoFuelPerTick + extraFuelPerTick;

                    double totalDamage = deteriorationService.calculate(cargo);
                    double conditionPerTick = (totalDamage / duration) * shipConditionMultiplier;

                    ship.setFuelLevel(Math.max(0.0, ship.getFuelLevel() - fuelPerTick));
                    ship.setCondition(Math.max(0.0, ship.getCondition() - conditionPerTick));

                    shipRepository.save(ship);
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