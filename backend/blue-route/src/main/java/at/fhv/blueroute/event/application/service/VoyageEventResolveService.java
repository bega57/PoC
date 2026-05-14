package at.fhv.blueroute.event.application.service;

import at.fhv.blueroute.event.application.exception.InvalidVoyageEventActionException;
import at.fhv.blueroute.event.application.exception.VoyageEventNotFoundException;
import at.fhv.blueroute.event.domain.model.VoyageEventOption;
import at.fhv.blueroute.event.domain.model.VoyageEventType;
import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import at.fhv.blueroute.common.websocket.SessionStatusMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.travel.client.TravelServiceClient;
import at.fhv.blueroute.travel.client.dto.VoyageResponse;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class VoyageEventResolveService {

    private static final double EXTRA_FUEL_LOSS_PER_DELAY_TICK = 5.0;
    private static final double EXTRA_CONDITION_LOSS_PER_DELAY_TICK = 2.0;

    private final ShipServiceClient shipServiceClient;
    private final JpaSessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;
    private final PlayerServiceClient playerServiceClient;
    private final TravelServiceClient travelServiceClient;

    public VoyageEventResolveService(ShipServiceClient shipServiceClient,
                                     JpaSessionRepository sessionRepository,
                                     WebSocketSender webSocketSender,
                                     PlayerServiceClient playerServiceClient,
                                     TravelServiceClient travelServiceClient) {
        this.shipServiceClient = shipServiceClient;
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
        this.playerServiceClient = playerServiceClient;
        this.travelServiceClient = travelServiceClient;
    }

    public String resolveEvent(Long voyageId, VoyageEventOption selectedOption) {
            VoyageResponse voyage = travelServiceClient.getVoyage(voyageId);

            if (voyage == null) {
                throw new VoyageEventNotFoundException(voyageId);
            }
        if (voyage.getPendingEventType() == null) {
            throw new VoyageEventNotFoundException(voyageId);
        }

        if (voyage.isEventResolved()) {
            throw new InvalidVoyageEventActionException("Event has already been resolved.");
        }

        if (selectedOption == null) {
            throw new InvalidVoyageEventActionException("Selected option must not be null.");
        }

        ShipResponse ship = shipServiceClient.getShip(
                voyage.getShipId()
        );

        if (ship == null) {
            throw new InvalidVoyageEventActionException(
                    "Ship not found for voyage with id: " + voyageId
            );
        }

        Long playerId = ship.getOwnerId();

        String resultMessage = applyConsequence(
                voyage,
                ship,
                playerId,
                voyage.getPendingEventType(),
                selectedOption
        );

        travelServiceClient.markEventResolved(voyageId, resultMessage);

        Session session = sessionRepository.findById(voyage.getSessionId())
                .orElseThrow(() -> new InvalidVoyageEventActionException("Session not found for voyage."));

        session.setStatus(SessionStatus.RUNNING);

        webSocketSender.sendSessionUpdate(
                session.getSessionCode(),
                new SessionStatusMessage(
                        "SESSION_RUNNING",
                        session.getSessionCode(),
                        "RUNNING"
                )
        );

        return resultMessage;
    }

    private String applyConsequence(VoyageResponse voyage,
                                    ShipResponse ship,
                                    Long playerId,
                                    VoyageEventType eventType,
                                    VoyageEventOption option) {
        return switch (eventType) {
            case BURNING_BARRELS -> resolveBurningBarrels(voyage, ship, option);
            case PIRATE_DRIP_CHECK -> resolvePirateDripCheck(voyage, ship, playerId, option);
            case RAT_BUFFET -> resolveRatBuffet(voyage, playerId, option);
            case HACKER_SEAGULLS -> resolveHackerSeagulls(voyage, ship, playerId, option);
            case MEDICAL_EMERGENCY -> resolveMedicalEmergency(voyage, playerId, option);
            case BAD_WEATHER -> resolveBadWeather(voyage, ship, option);
        };
    }

    private String resolveBurningBarrels(VoyageResponse voyage, ShipResponse ship, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                damageShip(ship, 8);
                delayVoyage(voyage, ship, 1);
                yield "The crew put out the fire. The ship took light damage and arrived 1 day later.";
            }
            case OPTION_B -> {
                damageShip(ship, 18);
                yield "Bold choice. You kept sailing, but the ship took heavy damage.";
            }
            case OPTION_C -> {
                reduceReward(voyage, 25);
                yield "You threw some oil barrels overboard. The ship was safe, but the reward was reduced.";
            }
        };
    }

    private String resolvePirateDripCheck(VoyageResponse voyage, ShipResponse ship, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(playerId, 500);
                travelServiceClient.setEventCost(voyage.getId(), 500.0);
                yield "The pirates accepted the bribe and called you honorary captains.";
            }
            case OPTION_B -> {
                delayVoyage(voyage, ship, 2);
                yield "You escaped successfully, but the voyage took 2 extra days.";
            }
            case OPTION_C -> {
                reduceReward(voyage, 15);
                yield "The cargo was hidden, but some luxury goods were damaged.";
            }
        };
    }

    private String resolveRatBuffet(VoyageResponse voyage, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(playerId, 200);
                travelServiceClient.setEventCost(voyage.getId(), 200.0);
                yield "You bought rat traps. Expensive, but the cargo buffet was closed.";
            }
            case OPTION_B -> {
                reduceReward(voyage, 20);
                yield "The rats kept partying. Part of the cargo was lost.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private String resolveHackerSeagulls(VoyageResponse voyage, ShipResponse ship, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                delayVoyage(voyage, ship, 1);
                yield "System reboot successful. Unfortunately, the voyage took 1 extra day.";
            }
            case OPTION_B -> {
                chargePlayer(playerId, 350);
                travelServiceClient.setEventCost(voyage.getId(), 350.0);
                yield "The technician removed three USB sticks from a seagull nest. Problem solved.";
            }
            case OPTION_C -> {
                damageShip(ship, 12);
                yield "Ignoring hacker seagulls was not smart. The ship electronics took damage.";
            }
        };
    }

    private String resolveMedicalEmergency(VoyageResponse voyage, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(playerId, 300);
                travelServiceClient.setEventCost(voyage.getId(), 300.0);
                yield "You ordered replacement medicine. The crew was relieved. Your wallet was not.";
            }
            case OPTION_B -> {
                reduceReward(voyage, 18);
                yield "You accepted the loss. Less usable medicine arrived at the destination.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private String resolveBadWeather(VoyageResponse voyage, ShipResponse ship, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                delayVoyage(voyage, ship, 2);
                yield "You sailed around the storm. It was safe, but the voyage took 2 extra days.";
            }
            case OPTION_B -> {
                damageShip(ship, 15);
                yield "You sailed through the storm. The ship took damage.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private void delayVoyage(VoyageResponse voyage, ShipResponse ship, int ticks) {
        double extraFuelLoss = ticks * EXTRA_FUEL_LOSS_PER_DELAY_TICK;
        double extraConditionLoss = ticks * EXTRA_CONDITION_LOSS_PER_DELAY_TICK;

        reduceFuel(ship, extraFuelLoss);
        damageShip(ship, extraConditionLoss);

        travelServiceClient.delayVoyage(
                voyage.getId(),
                ticks,
                extraFuelLoss,
                extraConditionLoss
        );
    }

    private void damageShip(ShipResponse ship, double damage) {
        int currentCondition = ship.getCondition() == null ? 100 : ship.getCondition();
        int newCondition = (int) Math.max(0, currentCondition - damage);
        ship.setCondition(newCondition);
    }

    private void reduceFuel(ShipResponse ship, double amount) {
        int currentFuel = ship.getFuelLevel() == null ? 100 : ship.getFuelLevel();
        int newFuelLevel = (int) Math.max(0, currentFuel - amount);
        ship.setFuelLevel(newFuelLevel);
    }

    private void reduceReward(VoyageResponse voyage, double percent) {
        travelServiceClient.reduceReward(voyage.getId(), percent);
    }

    private void chargePlayer(Long playerId, double amount) {
        if (playerId == null) {
            throw new InvalidVoyageEventActionException("This option requires a ship owner.");
        }

        playerServiceClient.updateBalance(
                playerId,
                -amount,
                "EVENT_PENALTY"
        );
    }
}