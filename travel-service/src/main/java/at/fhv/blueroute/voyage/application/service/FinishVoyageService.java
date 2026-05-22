package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
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

        Cargo cargo = cargoRepository.findById(voyage.getCargoId())
                .orElseThrow(() ->
                        new RuntimeException("Cargo not found"));

        shipServiceClient.finishVoyage(
                ship.getId(),
                voyage.getDestinationPort(),
                cargo.getRequiredCapacity()
        );

        // ==================== CUSTOMS CHECK ====================
        // Random customs check (~40% chance), regardless of smuggling
        boolean customsCheck = random.nextInt(100) < 40; //für immer custom checks 40 auf 100 ändern zum testen
        voyage.setCustomsChecked(customsCheck);

        if (customsCheck && voyage.isSmuggling()) {
            // Smuggling detected? (~60% chance if checked)
            boolean detected = random.nextInt(100) < 60; //für immer custom checks 60 auf 100 ändern zum testen
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

        if (!voyage.isRewardGranted()) {

            playerServiceClient.updateBalance(
                    ship.getOwnerId(),
                    voyage.getReward(),
                    "VOYAGE_REWARD"
            );

            voyage.setRewardGranted(true);
        }

        voyage.setStatus(VoyageStatus.FINISHED);

        return voyageRepository.save(voyage);
    }
}
