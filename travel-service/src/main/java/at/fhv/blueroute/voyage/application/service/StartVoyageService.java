package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.config.CargoDataLoader;
import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import at.fhv.blueroute.voyage.application.exception.VoyageException;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Transactional
@Service
public class StartVoyageService {

    private final JpaVoyageRepository voyageRepository;
    private final ShipServiceClient shipServiceClient;
    private final JpaCargoRepository cargoRepository;
    private final PlayerServiceClient playerServiceClient;
    private final VoyageEventPlanningService voyageEventPlanningService;
    private final JpaPortRepository portRepository;

    public StartVoyageService(
            JpaVoyageRepository voyageRepository, ShipServiceClient shipServiceClient,
            JpaCargoRepository cargoRepository,
            PlayerServiceClient playerServiceClient,
            VoyageEventPlanningService voyageEventPlanningService,
            JpaPortRepository portRepository
    ) {
        this.voyageRepository = voyageRepository;
        this.shipServiceClient = shipServiceClient;
        this.cargoRepository = cargoRepository;
        this.playerServiceClient = playerServiceClient;
        this.voyageEventPlanningService = voyageEventPlanningService;
        this.portRepository = portRepository;
    }

    public Voyage startVoyage(
            Long shipId,
            Long cargoId,
            Long sessionId,
            int currentTick,
            boolean smuggling
    ) {

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

        voyage.setSessionId(sessionId);

        int requiredTicks = cargo.getRequiredTicks();
        int tickModifier = resolveTickModifier(ship.getSpeedCategory());
        int adjustedTicks = Math.max(2, requiredTicks + tickModifier);

        double fuelPerTick =
                cargo.getFuelConsumption() / (double) adjustedTicks;

        double conditionPerTick =
                cargo.getConditionDamage() / (double) adjustedTicks;

        voyage.setFuelPerTick(fuelPerTick);
        voyage.setConditionPerTick(conditionPerTick);

        voyage.setStartTick(currentTick);
        voyage.setArrivalTick(currentTick + adjustedTicks);

        voyageEventPlanningService
                .planEventForVoyage(
                        cargo,
                        currentTick,
                        voyage.getArrivalTick()
                )
                .ifPresent(plannedEvent -> {
                    voyage.setPendingEventType(plannedEvent.eventType());
                    voyage.setEventTriggerTick(plannedEvent.eventTriggerTick());
                    voyage.setEventTriggered(false);
                    voyage.setEventResolved(false);
                });

        System.out.println("SET StartTick: " + voyage.getStartTick());
        System.out.println("SET ArrivalTick: " + voyage.getArrivalTick());

        voyage.setReward(cargo.getReward());
        voyage.setRewardGranted(false);

        // ==================== SMUGGLING ====================
        voyage.setSmuggling(smuggling);
        if (smuggling) {
            double smugglingBonus = cargo.getReward() * 0.3;
            voyage.setSmugglingReward(smugglingBonus);
            System.out.println("SMUGGLING ACCEPTED - bonus: " + smugglingBonus);
        }
        // ===================================================

        shipServiceClient.startVoyage(
                ship.getId(),
                cargo.getRequiredCapacity()
        );

        Voyage savedVoyage = voyageRepository.save(voyage);

        return savedVoyage;
    }

    public Voyage startEmptyVoyage(
            Long shipId,
            String destinationPortName,
            Long sessionId,
            int currentTick
    ) {
        ShipResponse ship = shipServiceClient.getShip(shipId);

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
        if (ship.getCurrentPort() == null) {
            throw new VoyageException("Ship is currently not in a port");
        }
        if (ship.getCurrentPort().equals(destinationPortName)) {
            throw new VoyageException("Ship is already at " + destinationPortName);
        }

        Port origin = portRepository.findByName(ship.getCurrentPort())
                .orElseThrow(() -> new VoyageException("Origin port not found: " + ship.getCurrentPort()));
        Port destination = portRepository.findByName(destinationPortName)
                .orElseThrow(() -> new VoyageException("Destination port not found: " + destinationPortName));

        int distance = CargoDataLoader.DistanceCalculator.calculate(origin, destination);
        int requiredTicks = Math.max(2, (int) Math.ceil(distance / 1400.0));
        int tickModifier = resolveTickModifier(ship.getSpeedCategory());
        int adjustedTicks = Math.max(2, requiredTicks + tickModifier);

        // Empty voyage: base fuel (no cargo weight, LOW risk equivalent)
        double totalFuel = Math.round(distance * 0.0004 * 1.0 * 1.2 * 100.0) / 100.0;
        if (ship.getFuelLevel() < totalFuel) {
            throw new VoyageException("Not enough fuel for this voyage");
        }

        // Condition damage: ticks * 0.08 * LOW risk factor (0.8)
        double totalCondition = Math.round(adjustedTicks * 0.08 * 0.8 * 100.0) / 100.0;

        double fuelPerTick = totalFuel / (double) adjustedTicks;
        double conditionPerTick = totalCondition / (double) adjustedTicks;

        LocalDateTime now = LocalDateTime.now();
        Voyage voyage = new Voyage();
        voyage.setShipId(shipId);
        voyage.setCargoId(null);
        voyage.setOriginPort(ship.getCurrentPort());
        voyage.setDestinationPort(destinationPortName);
        voyage.setStatus(VoyageStatus.RUNNING);
        voyage.setStartTime(now);
        voyage.setSessionId(sessionId);
        voyage.setStartTick(currentTick);
        voyage.setArrivalTick(currentTick + adjustedTicks);
        voyage.setFuelPerTick(fuelPerTick);
        voyage.setConditionPerTick(conditionPerTick);
        voyage.setReward(0);
        voyage.setRewardGranted(true); // no reward to grant
        voyage.setSmuggling(false);

        // No event planning for empty voyages
        shipServiceClient.startVoyage(ship.getId(), 0);

        return voyageRepository.save(voyage);
    }

    private int resolveTickModifier(String speedCategory) {
        if (speedCategory == null) return 0;
        return switch (speedCategory) {
            case "SLOW" -> +2;
            case "FAST" -> -1;
            default -> 0;
        };
    }

}
