package at.fhv.blueroute.event.application.service;

import at.fhv.blueroute.event.application.exception.InvalidVoyageEventActionException;
import at.fhv.blueroute.event.application.exception.VoyageEventNotFoundException;
import at.fhv.blueroute.event.domain.model.VoyageEventOption;
import at.fhv.blueroute.event.domain.model.VoyageEventType;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.ship.application.exception.InsufficientBalanceException;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import at.fhv.blueroute.common.websocket.SessionStatusMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class VoyageEventResolveService {

    private static final double EXTRA_FUEL_LOSS_PER_DELAY_TICK = 5.0;
    private static final double EXTRA_CONDITION_LOSS_PER_DELAY_TICK = 2.0;

    private final JpaVoyageRepository voyageRepository;
    private final JpaShipRepository shipRepository;
    private final JpaSessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;

    public VoyageEventResolveService(JpaVoyageRepository voyageRepository,
                                     JpaShipRepository shipRepository,
                                     JpaSessionRepository sessionRepository,
                                     WebSocketSender webSocketSender) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
    }

    public String resolveEvent(Long voyageId, VoyageEventOption selectedOption) {
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new VoyageEventNotFoundException(voyageId));

        if (voyage.getPendingEventType() == null) {
            throw new VoyageEventNotFoundException(voyageId);
        }

        if (!voyage.isEventTriggered()) {
            voyage.setEventTriggered(true);
        }

        if (voyage.isEventResolved()) {
            throw new InvalidVoyageEventActionException("Event has already been resolved.");
        }

        if (selectedOption == null) {
            throw new InvalidVoyageEventActionException("Selected option must not be null.");
        }

        Ship ship = shipRepository.findById(voyage.getShipId())
                .orElseThrow(() -> new InvalidVoyageEventActionException(
                        "Ship not found for voyage with id: " + voyageId
                ));

        Player player = ship.getOwner();

        String resultMessage = applyConsequence(
                voyage,
                ship,
                player,
                voyage.getPendingEventType(),
                selectedOption
        );

        voyage.setEventResultMessage(resultMessage);

        voyage.setEventResolved(true);

        shipRepository.save(ship);
        voyageRepository.save(voyage);

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

    private String applyConsequence(Voyage voyage,
                                    Ship ship,
                                    Player player,
                                    VoyageEventType eventType,
                                    VoyageEventOption option) {
        return switch (eventType) {
            case BURNING_BARRELS -> resolveBurningBarrels(voyage, ship, option);
            case PIRATE_DRIP_CHECK -> resolvePirateDripCheck(voyage, ship, player, option);
            case RAT_BUFFET -> resolveRatBuffet(voyage, player, option);
            case HACKER_SEAGULLS -> resolveHackerSeagulls(voyage, ship, player, option);
            case MEDICAL_EMERGENCY -> resolveMedicalEmergency(voyage, player, option);
            case BAD_WEATHER -> resolveBadWeather(voyage, ship, option);
        };
    }

    private String resolveBurningBarrels(Voyage voyage, Ship ship, VoyageEventOption option) {
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

    private String resolvePirateDripCheck(Voyage voyage, Ship ship, Player player, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(player, 500);
                voyage.setEventCost(500.0);
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

    private String resolveRatBuffet(Voyage voyage, Player player, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(player, 200);
                voyage.setEventCost(200.0);
                yield "You bought rat traps. Expensive, but the cargo buffet was closed.";
            }
            case OPTION_B -> {
                reduceReward(voyage, 20);
                yield "The rats kept partying. Part of the cargo was lost.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private String resolveHackerSeagulls(Voyage voyage, Ship ship, Player player, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                delayVoyage(voyage, ship, 1);
                yield "System reboot successful. Unfortunately, the voyage took 1 extra day.";
            }
            case OPTION_B -> {
                chargePlayer(player, 350);
                voyage.setEventCost(350.0);
                yield "The technician removed three USB sticks from a seagull nest. Problem solved.";
            }
            case OPTION_C -> {
                damageShip(ship, 12);
                yield "Ignoring hacker seagulls was not smart. The ship electronics took damage.";
            }
        };
    }

    private String resolveMedicalEmergency(Voyage voyage, Player player, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(player, 300);
                voyage.setEventCost(300.0);
                yield "You ordered replacement medicine. The crew was relieved. Your wallet was not.";
            }
            case OPTION_B -> {
                reduceReward(voyage, 18);
                yield "You accepted the loss. Less usable medicine arrived at the destination.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private String resolveBadWeather(Voyage voyage, Ship ship, VoyageEventOption option) {
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

    private void delayVoyage(Voyage voyage, Ship ship, int ticks) {
        voyage.setArrivalTick(voyage.getArrivalTick() + ticks);

        double extraFuelLoss = ticks * EXTRA_FUEL_LOSS_PER_DELAY_TICK;
        double extraConditionLoss = ticks * EXTRA_CONDITION_LOSS_PER_DELAY_TICK;

        reduceFuel(ship, extraFuelLoss);
        damageShip(ship, extraConditionLoss);

        voyage.setExtraDelayTicks(voyage.getExtraDelayTicks() + ticks);
        voyage.setExtraFuelLoss(voyage.getExtraFuelLoss() + extraFuelLoss);
        voyage.setExtraConditionLoss(voyage.getExtraConditionLoss() + extraConditionLoss);
    }

    private void damageShip(Ship ship, double damage) {
        int currentCondition = ship.getCondition() == null ? 100 : ship.getCondition();
        int newCondition = (int) Math.max(0, currentCondition - damage);
        ship.setCondition(newCondition);
    }

    private void reduceFuel(Ship ship, double amount) {
        int currentFuel = ship.getFuelLevel() == null ? 100 : ship.getFuelLevel();
        int newFuelLevel = (int) Math.max(0, currentFuel - amount);
        ship.setFuelLevel(newFuelLevel);
    }

    private void reduceReward(Voyage voyage, double percent) {
        double factor = (100.0 - percent) / 100.0;
        voyage.setReward(voyage.getReward() * factor);
        voyage.setRewardLossPercent(voyage.getRewardLossPercent() + percent);
    }

    private void chargePlayer(Player player, double amount) {
        if (player == null) {
            throw new InvalidVoyageEventActionException("This option requires a ship owner.");
        }
        double currentBalance = player.getBalance() == null ? 0.0 : player.getBalance();

        if (currentBalance < amount) {
            throw new InsufficientBalanceException("Not enough balance to choose this option.");
        }

        player.setBalance(currentBalance - amount);
    }
}