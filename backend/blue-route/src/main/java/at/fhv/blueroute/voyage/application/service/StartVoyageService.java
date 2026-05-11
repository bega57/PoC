package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.event.application.service.VoyageEventPlanningService;
import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import at.fhv.blueroute.voyage.application.exception.VoyageException;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import at.fhv.blueroute.common.websocket.VoyageStartedMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
public class StartVoyageService {

    private final JpaVoyageRepository voyageRepository;
    private final ShipServiceClient shipServiceClient;
    private final JpaCargoRepository cargoRepository;
    private final JpaSessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;
    private final VoyageEventPlanningService voyageEventPlanningService;
    private final PlayerServiceClient playerServiceClient;

    public StartVoyageService(
            JpaVoyageRepository voyageRepository, ShipServiceClient shipServiceClient,
            JpaCargoRepository cargoRepository,
            JpaSessionRepository sessionRepository,
            WebSocketSender webSocketSender,
            VoyageEventPlanningService voyageEventPlanningService,
            PlayerServiceClient playerServiceClient
    ) {
        this.voyageRepository = voyageRepository;
        this.shipServiceClient = shipServiceClient;
        this.cargoRepository = cargoRepository;
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
        this.voyageEventPlanningService = voyageEventPlanningService;
        this.playerServiceClient = playerServiceClient;
    }

    public Voyage startVoyage(Long shipId, Long cargoId, String sessionCode) {

        ShipResponse ship =
                shipServiceClient.getShip(shipId);

        if (ship.isTraveling()) {
            throw new VoyageException("Ship is already traveling");
        }

        if (ship.getCondition() <= 0) {
            throw new VoyageException("Ship is broken and needs repair");
        }

        if (ship.getCondition() < 20) {
            throw new VoyageException("Ship condition too low for voyage");
        }

        boolean isBusy = voyageRepository
                .existsByShipIdAndStatusNot(shipId, VoyageStatus.FINISHED);

        if (isBusy) {
            throw new VoyageException("Ship already has an active voyage");
        }

        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new RuntimeException("Cargo not found"));

        if (ship.getCurrentPort() == null) {
            throw new VoyageException("Ship is currently not in a port");
        }

        if (!ship.getCurrentPort().equals(cargo.getOriginPort().getName())) {
            throw new VoyageException(
                    "Ship is in " + ship.getCurrentPort() +
                            " but cargo starts in " + cargo.getOriginPort().getName()
            );
        }

        if (ship.getCargoCapacity() - ship.getUsedCapacity() < cargo.getRequiredCapacity()) {
            throw new VoyageException("Ship does not have enough cargo capacity");
        }

        if (ship.getOwnerId() == null) {
            throw new VoyageException("Ship owner not found");
        }

        if (ship.getFuelLevel() < cargo.getFuelConsumption()) {
            throw new VoyageException("Not enough fuel for this voyage");
        }

        Long playerId = ship.getOwnerId();

        playerServiceClient.updateBalance(
                playerId,
                -cargo.getPrice(),
                "VOYAGE_START"
        );
        LocalDateTime now = LocalDateTime.now();

        Voyage voyage = new Voyage();
        voyage.setShipId(shipId);
        voyage.setCargoId(cargo.getId());
        voyage.setOriginPort(cargo.getOriginPort().getName());
        voyage.setDestinationPort(cargo.getDestinationPort().getName());
        voyage.setStatus(VoyageStatus.RUNNING);
        voyage.setStartTime(now);

        Session session = sessionRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new VoyageException("Session not found"));

        voyage.setSessionId(session.getId());

        int currentTick = sessionRepository
                .findById(session.getId())
                .orElseThrow()
                .getCurrentTick();
        int requiredTicks = cargo.getRequiredTicks();
        int speed = ship.getSpeed();

        System.out.println("---- VOYAGE DEBUG ----");
        System.out.println("CurrentTick: " + currentTick);
        System.out.println("RequiredTicks (Cargo): " + requiredTicks);
        System.out.println("Ship Speed: " + speed);
        System.out.println("Fuel Consumption (Cargo): " + cargo.getFuelConsumption());

        int adjustedTicks = requiredTicks;

        voyage.setStartTick(currentTick);
        voyage.setArrivalTick(currentTick + adjustedTicks);

        System.out.println("SET StartTick: " + voyage.getStartTick());
        System.out.println("SET ArrivalTick: " + voyage.getArrivalTick());

        voyage.setReward(cargo.getReward());
        voyage.setRewardGranted(false);

        voyageEventPlanningService.planEventForVoyage(voyage, cargo, currentTick);

        shipServiceClient.startVoyage(
                ship.getId(),
                cargo.getRequiredCapacity()
        );

        Voyage savedVoyage = voyageRepository.save(voyage);

        webSocketSender.sendSessionUpdate(
                session.getSessionCode(),
                new VoyageStartedMessage(
                        "VOYAGE_STARTED",
                        session.getSessionCode(),
                        ship.getId(),
                        cargo.getOriginPort().getName(),
                        cargo.getDestinationPort().getName(),
                        cargo.getRequiredTicks()
                )
        );

        return savedVoyage;
    }

}