package at.fhv.blueroute.event.application.service;

import at.fhv.blueroute.event.application.exception.InvalidVoyageEventActionException;
import at.fhv.blueroute.event.application.exception.VoyageEventNotFoundException;
import at.fhv.blueroute.event.domain.model.VoyageEventOption;
import at.fhv.blueroute.event.domain.model.VoyageEventType;
import at.fhv.blueroute.event.player.client.PlayerServiceClient;
import at.fhv.blueroute.event.session.client.SessionServiceClient;
import at.fhv.blueroute.event.ship.client.ShipServiceClient;
import at.fhv.blueroute.event.ship.client.dto.ShipResponse;
import at.fhv.blueroute.event.voyage.client.VoyageServiceClient;
import at.fhv.blueroute.event.voyage.client.dto.VoyageResponse;
import org.springframework.stereotype.Service;

@Service
public class VoyageEventResolveService {

    private static final double EXTRA_FUEL_LOSS_PER_DELAY_TICK = 5.0;
    private static final double EXTRA_CONDITION_LOSS_PER_DELAY_TICK = 2.0;

    private final VoyageServiceClient voyageServiceClient;
    private final ShipServiceClient shipServiceClient;
    private final PlayerServiceClient playerServiceClient;
    private final SessionServiceClient sessionServiceClient;

    public VoyageEventResolveService(VoyageServiceClient voyageServiceClient,
                                     ShipServiceClient shipServiceClient,
                                     PlayerServiceClient playerServiceClient,
                                     SessionServiceClient sessionServiceClient) {
        this.voyageServiceClient = voyageServiceClient;
        this.shipServiceClient = shipServiceClient;
        this.playerServiceClient = playerServiceClient;
        this.sessionServiceClient = sessionServiceClient;
    }

    public String resolveEvent(Long voyageId, VoyageEventOption selectedOption) {
        System.out.println("RESOLVE CALLED for voyage: " + voyageId + " option: " + selectedOption);

        VoyageResponse voyage = voyageServiceClient.getVoyage(voyageId);

        if (voyage == null) {
            throw new VoyageEventNotFoundException(voyageId);
        }
        if (voyage.getPendingEventType() == null) {
            throw new VoyageEventNotFoundException(voyageId);
        }
        if (voyage.isEventResolved()) {
            // Recovery: session may have stayed PAUSED if resumeAfterEvent previously failed
            try {
                sessionServiceClient.resumeAfterEvent(voyage.getSessionId());
                System.out.println("RECOVERY: resumed session " + voyage.getSessionId() + " for already-resolved event");
            } catch (Exception e) {
                System.err.println("RECOVERY resumeAfterEvent failed: " + e.getMessage());
            }
            throw new InvalidVoyageEventActionException("Event has already been resolved.");
        }
        if (selectedOption == null) {
            throw new InvalidVoyageEventActionException("Selected option must not be null.");
        }

        ShipResponse ship = shipServiceClient.getShip(voyage.getShipId());
        if (ship == null) {
            throw new InvalidVoyageEventActionException("Ship not found for voyage: " + voyageId);
        }

        Long playerId = ship.getOwnerId();

        // Apply consequence and mark event resolved
        String resultMessage;
        try {
            resultMessage = applyConsequence(
                    voyage, ship, playerId,
                    voyage.getPendingEventType(),
                    selectedOption
            );
            System.out.println("CONSEQUENCE APPLIED: " + resultMessage);
        } catch (Exception e) {
            System.err.println("CONSEQUENCE FAILED: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            throw e;
        }

        voyageServiceClient.markEventResolved(voyageId, resultMessage);

        // Resume session separately — a failure here must not block the success response,
        // because the event is already marked resolved and the player cannot retry.
        // The "already resolved" recovery path above will attempt resumeAfterEvent again
        // on the next resolve call (e.g. from the other player).
        try {
            sessionServiceClient.resumeAfterEvent(voyage.getSessionId());
        } catch (Exception e) {
            System.err.println("Failed to resume session " + voyage.getSessionId()
                    + " after event resolution — session may stay PAUSED: " + e.getMessage());
        }

        return resultMessage;
    }

    private String applyConsequence(VoyageResponse voyage, ShipResponse ship, Long playerId,
                                    VoyageEventType eventType, VoyageEventOption option) {
        return switch (eventType) {
            case BURNING_BARRELS   -> resolveBurningBarrels(voyage, ship, option);
            case PIRATE_DRIP_CHECK -> resolvePirateDripCheck(voyage, ship, playerId, option);
            case RAT_BUFFET        -> resolveRatBuffet(voyage, playerId, option);
            case HACKER_SEAGULLS   -> resolveHackerSeagulls(voyage, ship, playerId, option);
            case MEDICAL_EMERGENCY -> resolveMedicalEmergency(voyage, playerId, option);
            case BAD_WEATHER       -> resolveBadWeather(voyage, ship, option);
            case PILOT_STRIKE      -> resolvePilotStrike(voyage, ship, playerId, option);
        };
    }

    private String resolveBurningBarrels(VoyageResponse voyage, ShipResponse ship, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                damageShip(ship.getId(), 0, 8);
                delayVoyage(voyage, ship, 1);
                yield "The crew put out the fire. The ship took light damage and arrived 1 day later.";
            }
            case OPTION_B -> {
                damageShip(ship.getId(), 0, 18);
                yield "Bold choice. You kept sailing, but the ship took heavy damage.";
            }
            case OPTION_C -> {
                voyageServiceClient.reduceReward(voyage.getId(), 25);
                yield "You threw the barrels overboard. Safe ship, but reduced reward.";
            }
        };
    }

    private String resolvePirateDripCheck(VoyageResponse voyage, ShipResponse ship, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(playerId, 500);
                voyageServiceClient.setEventCost(voyage.getId(), 500.0);
                yield "The pirates accepted the bribe and called you honorary captains.";
            }
            case OPTION_B -> {
                delayVoyage(voyage, ship, 2);
                yield "You escaped, but the voyage took 2 extra days.";
            }
            case OPTION_C -> {
                voyageServiceClient.reduceReward(voyage.getId(), 15);
                yield "The cargo was hidden, but some luxury goods were damaged.";
            }
        };
    }

    private String resolveRatBuffet(VoyageResponse voyage, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(playerId, 200);
                voyageServiceClient.setEventCost(voyage.getId(), 200.0);
                yield "You bought rat traps. Expensive, but effective.";
            }
            case OPTION_B -> {
                voyageServiceClient.reduceReward(voyage.getId(), 20);
                yield "The rats kept partying. Part of the cargo was lost.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private String resolveHackerSeagulls(VoyageResponse voyage, ShipResponse ship, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                delayVoyage(voyage, ship, 1);
                yield "System reboot successful. The voyage took 1 extra day.";
            }
            case OPTION_B -> {
                chargePlayer(playerId, 350);
                voyageServiceClient.setEventCost(voyage.getId(), 350.0);
                yield "The technician solved the problem. Expensive but quick.";
            }
            case OPTION_C -> {
                damageShip(ship.getId(), 0, 12);
                yield "Ignoring hacker seagulls was not smart. Ship electronics damaged.";
            }
        };
    }

    private String resolveMedicalEmergency(VoyageResponse voyage, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(playerId, 300);
                voyageServiceClient.setEventCost(voyage.getId(), 300.0);
                yield "Replacement medicine ordered. Crew relieved, wallet not.";
            }
            case OPTION_B -> {
                voyageServiceClient.reduceReward(voyage.getId(), 18);
                yield "You accepted the loss. Less medicine arrived.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private String resolveBadWeather(VoyageResponse voyage, ShipResponse ship, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                delayVoyage(voyage, ship, 2);
                yield "You sailed around the storm. Safe but 2 days late.";
            }
            case OPTION_B -> {
                damageShip(ship.getId(), 0, 15);
                yield "You sailed through the storm. The ship took damage.";
            }
            case OPTION_C -> throw new InvalidVoyageEventActionException("This event only has two options.");
        };
    }

    private void delayVoyage(VoyageResponse voyage, ShipResponse ship, int ticks) {
        double fuelLoss = ticks * EXTRA_FUEL_LOSS_PER_DELAY_TICK;
        double conditionLoss = ticks * EXTRA_CONDITION_LOSS_PER_DELAY_TICK;
        shipServiceClient.applyDamage(ship.getId(), fuelLoss, conditionLoss);
        voyageServiceClient.delayVoyage(voyage.getId(), ticks, fuelLoss, conditionLoss);
    }

    private void damageShip(Long shipId, double fuelLoss, double conditionLoss) {
        shipServiceClient.applyDamage(shipId, fuelLoss, conditionLoss);
    }

    private void chargePlayer(Long playerId, double amount) {
        if (playerId == null) {
            throw new InvalidVoyageEventActionException("This option requires a ship owner.");
        }
        playerServiceClient.updateBalance(playerId, -amount, "EVENT_PENALTY");
    }

    private String resolvePilotStrike(VoyageResponse voyage, ShipResponse ship, Long playerId, VoyageEventOption option) {
        return switch (option) {
            case OPTION_A -> {
                chargePlayer(playerId, 700);
                voyageServiceClient.setEventCost(voyage.getId(), 700.0);
                yield "The pilots took the bribe and guided your ship in. Cost you 700 coins, but the ship is safe.";
            }
            case OPTION_B -> {
                yield "Impressive! You docked the ship yourself like a seasoned captain. No damage, no cost.";
            }
            case OPTION_C -> {
                damageShip(ship.getId(), 0, 40);
                yield "You misjudged the docking and scraped the hull badly. Ship condition -40!";
            }
        };
    }
}