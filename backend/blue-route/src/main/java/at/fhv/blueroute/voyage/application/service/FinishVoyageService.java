package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class FinishVoyageService {

    private final JpaVoyageRepository voyageRepository;
    private final JpaShipRepository shipRepository;
    private final PlayerServiceClient playerServiceClient;
    private final JpaCargoRepository cargoRepository;

    public FinishVoyageService(JpaVoyageRepository voyageRepository,
                               JpaShipRepository shipRepository,
                               PlayerServiceClient playerServiceClient,
                               JpaCargoRepository cargoRepository) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.playerServiceClient = playerServiceClient;
        this.cargoRepository = cargoRepository;
    }

    public void finishVoyage(Long voyageId, int currentTick) {

        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new RuntimeException("Voyage not found"));

        if (voyage.getStatus() == VoyageStatus.FINISHED) {
            return;
        }

        if (currentTick < voyage.getArrivalTick()) {
            System.out.println("NOT FINISHED YET → current=" + currentTick
                    + " arrival=" + voyage.getArrivalTick());
            return;
        }

        Ship ship = shipRepository.findById(voyage.getShipId())
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        Cargo cargo = cargoRepository.findById(voyage.getCargoId())
                .orElseThrow(() -> new RuntimeException("Cargo not found"));

        ship.setCurrentPort(voyage.getDestinationPort());
        ship.setTraveling(false);
        ship.setUsedCapacity(
                Math.max(0, ship.getUsedCapacity() - cargo.getRequiredCapacity())
        );

        if (!voyage.isRewardGranted()) {

            playerServiceClient.updateBalance(
                    ship.getOwnerId(),
                    voyage.getReward(),
                    "VOYAGE_REWARD"
            );

            voyage.setRewardGranted(true);
        }

        voyage.setStatus(VoyageStatus.FINISHED);

        shipRepository.save(ship);
        voyageRepository.save(voyage);
    }
}