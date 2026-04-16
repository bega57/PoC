package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
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
    private final JpaShipRepository shipRepository;
    private final JpaCargoRepository cargoRepository;
    private final PlayerRepository playerRepository;
    private final JpaSessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;

    public StartVoyageService(
            JpaVoyageRepository voyageRepository,
            JpaShipRepository shipRepository,
            JpaCargoRepository cargoRepository,
            PlayerRepository playerRepository,
            JpaSessionRepository sessionRepository,
            WebSocketSender webSocketSender
    ) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.cargoRepository = cargoRepository;
        this.playerRepository = playerRepository;
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
    }

    public Voyage startVoyage(Long shipId, Long cargoId, String sessionCode) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        if (ship.isTraveling()) {
            throw new VoyageException("Ship is already traveling");
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

        if (ship.getCargoCapacity() < cargo.getRequiredCapacity()) {
            throw new VoyageException("Ship does not have enough cargo capacity");
        }

        if (ship.getOwner() == null) {
            throw new VoyageException("Ship owner not found");
        }

        if (ship.getOwner().getBalance() < cargo.getPrice()) {
            throw new VoyageException("Not enough balance to start this voyage");
        }

        if (ship.getFuelLevel() < cargo.getFuelConsumption()) {
            throw new VoyageException("Not enough fuel for this voyage");
        }

        ship.getOwner().setBalance(ship.getOwner().getBalance() - cargo.getPrice());
        playerRepository.save(ship.getOwner());

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

        int currentTick = session.getCurrentTick();
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


        double fuelUsed = cargo.getFuelConsumption();
        ship.setFuelLevel((int)(ship.getFuelLevel() - fuelUsed));

        System.out.println("Fuel used: " + fuelUsed);
        System.out.println("Remaining fuel: " + ship.getFuelLevel());

        ship.setTraveling(true);
        ship.setCurrentPort(null);
        shipRepository.save(ship);

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