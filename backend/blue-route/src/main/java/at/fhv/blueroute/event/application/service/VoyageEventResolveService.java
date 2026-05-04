package at.fhv.blueroute.event.application.service;

import at.fhv.blueroute.event.application.exception.InvalidVoyageEventActionException;
import at.fhv.blueroute.event.application.exception.VoyageEventNotFoundException;
import at.fhv.blueroute.event.domain.model.VoyageEventOption;
import at.fhv.blueroute.event.domain.model.VoyageEventType;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.infrastructure.persistence.JpaPlayerRepository;
import at.fhv.blueroute.ship.application.exception.InsufficientBalanceException;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import org.springframework.stereotype.Service;

@Service
public class VoyageEventResolveService {

    private static final double EXTRA_FUEL_LOSS_PER_DELAY_TICK = 5.0;
    private static final double EXTRA_CONDITION_LOSS_PER_DELAY_TICK = 2.0;

    private final JpaVoyageRepository voyageRepository;
    private final JpaShipRepository shipRepository;
    private final JpaPlayerRepository playerRepository;

    public VoyageEventResolveService(JpaVoyageRepository voyageRepository,
                                     JpaShipRepository shipRepository,
                                     JpaPlayerRepository playerRepository) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.playerRepository = playerRepository;
    }

    public String resolveEvent(Long voyageId, VoyageEventOption selectedOption) {
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new VoyageEventNotFoundException(voyageId));

        if (voyage.getPendingEventType() == null) {
            throw new VoyageEventNotFoundException(voyageId);
        }

        if (!voyage.isEventTriggered()) {
            throw new InvalidVoyageEventActionException("Event has not been triggered yet.");
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

        if (player == null) {
            throw new InvalidVoyageEventActionException(
                    "Ship has no owner. Ship id: " + ship.getId()
            );
        }

        String resultMessage = applyConsequence(
                voyage,
                ship,
                player,
                voyage.getPendingEventType(),
                selectedOption
        );

        voyage.setEventResolved(true);

        shipRepository.save(ship);
        playerRepository.save(player);
        voyageRepository.save(voyage);

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
                yield "The crew puts out the fire. The ship takes light damage and arrives 1 day later.";
            }
            case OPTION_B -> {
                damageShip(ship, 18);
                yield "Bold choice. You keep sailing, but the ship takes heavy damage.";
            }
            case OPTION_C -> {
                reduceReward(voyage, 25);
                yield "You throw some oil barrels overboard. The ship is safe, but the reward is reduced.";
            }
        };
    }

    private String resolvePirateDripCheck(Voyage voyage, Ship ship, Player player, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(player, 500);
                yield "The pirates accept the bribe and call you honorary captains.";
            }
            case OPTION_B -> {
                delayVoyage(voyage, ship, 2);
                yield "You escape successfully, but the voyage takes 2 extra days.";
            }
            case OPTION_C -> {
                reduceReward(voyage, 15);
                yield "The cargo is hidden, but some luxury goods are damaged.";
            }
        };
    }

    private String resolveRatBuffet(Voyage voyage, Player player, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(player, 200);
                yield "You buy rat traps. Expensive, but the cargo buffet is closed.";
            }
            case OPTION_B -> {
                reduceReward(voyage, 20);
                yield "The rats keep partying. Part of the cargo is lost.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private String resolveHackerSeagulls(Voyage voyage, Ship ship, Player player, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                delayVoyage(voyage, ship, 1);
                yield "System reboot successful. Unfortunately, the voyage takes 1 extra day.";
            }
            case OPTION_B -> {
                chargePlayer(player, 350);
                yield "The technician removes three USB sticks from a seagull nest. Problem solved.";
            }
            case OPTION_C -> {
                damageShip(ship, 12);
                yield "Ignoring hacker seagulls was not smart. The ship electronics take damage.";
            }
        };
    }

    private String resolveMedicalEmergency(Voyage voyage, Player player, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(player, 300);
                yield "You order replacement medicine. The crew is relieved. Your wallet is not.";
            }
            case OPTION_B -> {
                reduceReward(voyage, 18);
                yield "You accept the loss. Less usable medicine arrives at the destination.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private String resolveBadWeather(Voyage voyage, Ship ship, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                delayVoyage(voyage, ship, 2);
                yield "You sail around the storm. Safe, but the voyage takes 2 extra days.";
            }
            case OPTION_B -> {
                damageShip(ship, 15);
                yield "You sail through the storm. The ship takes damage.";
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
    }

    private void damageShip(Ship ship, double damage) {
        double currentCondition = ship.getCondition() == null ? 100.0 : ship.getCondition();
        double newCondition = Math.max(0, currentCondition - damage);
        ship.setCondition(newCondition);
    }

    private void reduceFuel(Ship ship, double amount) {
        double currentFuel = ship.getFuelLevel() == null ? 100.0 : ship.getFuelLevel();
        double newFuelLevel = Math.max(0, currentFuel - amount);
        ship.setFuelLevel(newFuelLevel);
    }

    private void reduceReward(Voyage voyage, double percent) {
        double factor = (100.0 - percent) / 100.0;
        voyage.setReward(voyage.getReward() * factor);
    }

    private void chargePlayer(Player player, double amount) {
        double currentBalance = player.getBalance() == null ? 0.0 : player.getBalance();

        if (currentBalance < amount) {
            throw new InsufficientBalanceException("Not enough balance to choose this option.");
        }

        player.setBalance(currentBalance - amount);
    }
}