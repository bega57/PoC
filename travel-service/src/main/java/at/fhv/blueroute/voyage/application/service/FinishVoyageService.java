package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Transactional
public class FinishVoyageService {

    private final JpaVoyageRepository voyageRepository;
    private final ShipServiceClient shipServiceClient;
    private final PlayerServiceClient playerServiceClient;
    private final JpaCargoRepository cargoRepository;

    private final Random random = new Random();

    public FinishVoyageService(JpaVoyageRepository voyageRepository,
                               ShipServiceClient shipServiceClient,
                               PlayerServiceClient playerServiceClient,
                               JpaCargoRepository cargoRepository) {
        this.voyageRepository = voyageRepository;
        this.shipServiceClient = shipServiceClient;
        this.playerServiceClient = playerServiceClient;
        this.cargoRepository = cargoRepository;
    }

    public Voyage finishVoyage(Long voyageId, int currentTick) {

        System.out.println("TRAVEL SERVICE FINISH CALLED");
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() ->
                        new RuntimeException("Voyage not found"));

        if (voyage.getStatus() == VoyageStatus.FINISHED) {
            return voyage;
        }

        if (currentTick < voyage.getArrivalTick()) {
            return voyage;
        }

        ShipResponse ship =
                shipServiceClient.getShip(voyage.getShipId());

        if (ship == null) {
            throw new RuntimeException("Ship not found for voyage " + voyageId
                    + " (shipId=" + voyage.getShipId() + ") — ship may have been deleted");
        }

        // Empty voyage has no cargo
        Cargo cargo = voyage.getCargoId() != null
                ? cargoRepository.findById(voyage.getCargoId())
                        .orElseThrow(() -> new RuntimeException("Cargo not found"))
                : null;

        shipServiceClient.finishVoyage(
                ship.getId(),
                voyage.getDestinationPort(),
                cargo != null ? cargo.getRequiredCapacity() : 0
        );

        // ==================== CUSTOMS CHECK ====================
        boolean luckyCloverActive = "LUCKY_CLOVER".equals(voyage.getActivePowerUp());

        // Random customs check (~40% chance), regardless of smuggling
        boolean customsCheck = !luckyCloverActive && random.nextInt(100) < 40;
        voyage.setCustomsChecked(customsCheck);

        if (customsCheck && voyage.isSmuggling()) {
            // Smuggling detected? (~60% chance if checked)
            boolean detected = random.nextInt(100) < 60;
            voyage.setSmugglingDetected(detected);

            if (detected) {
                // Calculate penalty (25% of reward) and detention (2-4 days)
                double penalty = voyage.getReward() * 0.25;
                int detentionTicks = 2 + random.nextInt(3);
                voyage.setSmugglingPenalty(penalty);
                voyage.setSmugglingDetentionTicks(detentionTicks);
                voyage.setSmugglingResolved(false);

                System.out.println("SMUGGLING DETECTED! Penalty: " + penalty
                        + ", Detention: " + detentionTicks + " days");
            }
        }

        // Pay smuggling bonus if smuggling was NOT detected
        if (voyage.isSmuggling() && !voyage.isSmugglingDetected()) {
            playerServiceClient.updateBalance(
                    ship.getOwnerId(),
                    voyage.getSmugglingReward(),
                    "SMUGGLING_REWARD"
            );
            voyage.setSmugglingResolved(true);
            System.out.println("SMUGGLING SUCCESS! Bonus paid: " + voyage.getSmugglingReward());
        }

        // If no smuggling or customs passed without issues, mark resolved
        if (!voyage.isSmuggling() || !voyage.isSmugglingDetected()) {
            voyage.setSmugglingResolved(true);
        }
        // ========================================================

        if (!voyage.isRewardGranted() && cargo != null) {

            playerServiceClient.updateBalance(
                    ship.getOwnerId(),
                    voyage.getReward(),
                    "VOYAGE_REWARD"
            );

            voyage.setRewardGranted(true);
        }

        // ==================== POINTS CALCULATION ====================
        if (cargo != null) {
            int basePoints = (int) (voyage.getReward() / 100);

            double riskMultiplier = 1.0;
            if (cargo.getRiskLevel() == RiskLevel.MEDIUM) riskMultiplier = 1.5;
            else if (cargo.getRiskLevel() == RiskLevel.HIGH) riskMultiplier = 2.0;

            int riskedPoints = (int) (basePoints * riskMultiplier);

            int bonus = 0;
            int penalty = 0;
            StringBuilder breakdown = new StringBuilder();
            breakdown.append("Base: +").append(basePoints).append(" pts");

            if (riskMultiplier > 1.0) {
                breakdown.append(" | Risk (").append(cargo.getRiskLevel()).append(" ×")
                        .append(riskMultiplier).append("): +").append(riskedPoints - basePoints).append(" pts");
            }

            boolean hasDelay = voyage.getExtraDelayTicks() != null && voyage.getExtraDelayTicks() > 0;
            boolean hasEvent = voyage.isEventTriggered();
            boolean turboCableActive = "TURBO_CABLE".equals(voyage.getActivePowerUp());

            // TURBO_CABLE: ignore delay penalty
            if (turboCableActive && hasDelay) {
                breakdown.append(" | ⚡ Turbo Cable: delay ignored");
                hasDelay = false;
            }

            if (!hasEvent) {
                bonus += (int) (riskedPoints * 0.2);
                breakdown.append(" | Clean voyage: +").append((int)(riskedPoints * 0.2)).append(" pts");
            } else if (hasDelay) {
                penalty += 10;
                breakdown.append(" | Event delay: -10 pts");
            }

            if (voyage.isSmuggling() && !voyage.isSmugglingDetected()) {
                bonus += (int) (riskedPoints * 0.5);
                breakdown.append(" | Smuggling bonus: +").append((int)(riskedPoints * 0.5)).append(" pts");
            }

            if (voyage.isSmugglingDetected()) {
                penalty += 30;
                breakdown.append(" | Smuggling caught: -30 pts");
            }

            int totalPoints = riskedPoints + bonus - penalty;
            if (totalPoints < 0) totalPoints = 0;

            // CHOCOLATE_CAKE: +50% points multiplier
            if ("CHOCOLATE_CAKE".equals(voyage.getActivePowerUp())) {
                int cakeBonus = (int) (totalPoints * 0.5);
                breakdown.append(" | 🍰 Chocolate Cake: +").append(cakeBonus).append(" pts");
                totalPoints += cakeBonus;
            }

            voyage.setEarnedPoints(totalPoints);
            voyage.setPointsBreakdown(breakdown.toString());

            playerServiceClient.addPoints(ship.getOwnerId(), totalPoints);
        }
        // ============================================================

        voyage.setStatus(VoyageStatus.FINISHED);

        return voyageRepository.save(voyage);
    }
}
