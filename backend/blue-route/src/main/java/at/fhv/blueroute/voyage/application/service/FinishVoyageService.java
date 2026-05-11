package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class FinishVoyageService {

    private final JpaVoyageRepository voyageRepository;
    private final ShipServiceClient shipServiceClient;
    private final PlayerServiceClient playerServiceClient;
    private final JpaCargoRepository cargoRepository;

    public FinishVoyageService(JpaVoyageRepository voyageRepository,
ShipServiceClient shipServiceClient,
                               PlayerServiceClient playerServiceClient,
                               JpaCargoRepository cargoRepository) {
        this.voyageRepository = voyageRepository;
        this.shipServiceClient = shipServiceClient;
        this.playerServiceClient = playerServiceClient;
        this.cargoRepository = cargoRepository;
    }

    public void finishVoyage(Long voyageId, int currentTick) {

        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() ->
                        new RuntimeException("Voyage not found"));

        if (voyage.getStatus() == VoyageStatus.FINISHED) {
            return;
        }

        if (currentTick < voyage.getArrivalTick()) {
            return;
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

        if (!voyage.isRewardGranted()) {

            playerServiceClient.updateBalance(
                    ship.getOwnerId(),
                    voyage.getReward(),
                    "VOYAGE_REWARD"
            );

            voyage.setRewardGranted(true);
        }

        voyage.setStatus(VoyageStatus.FINISHED);

        voyageRepository.save(voyage);
    }
}