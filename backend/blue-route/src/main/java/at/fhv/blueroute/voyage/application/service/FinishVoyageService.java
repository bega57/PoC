package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
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
    private final PlayerRepository playerRepository;

    public FinishVoyageService(JpaVoyageRepository voyageRepository,
                               JpaShipRepository shipRepository,
                               PlayerRepository playerRepository) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.playerRepository = playerRepository;
    }

    public void finishVoyage(Long voyageId) {

        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new RuntimeException("Voyage not found"));

        if (voyage.getStatus() == VoyageStatus.FINISHED) {
            return;
        }

        Ship ship = shipRepository.findById(voyage.getShipId())
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        ship.setTraveling(false);
        ship.setCurrentPort(voyage.getDestinationPort());

        Player owner = ship.getOwner();
        if (owner == null) {
            throw new RuntimeException("Ship owner not found");
        }

        System.out.println("Reward = " + voyage.getReward());
        System.out.println("Balance before finish = " + owner.getBalance());

        if (!voyage.isRewardGranted()) {
            owner.setBalance(owner.getBalance() + voyage.getReward());
            playerRepository.save(owner);
            voyage.setRewardGranted(true);
        }

        voyage.setStatus(VoyageStatus.FINISHED);

        shipRepository.save(ship);
        voyageRepository.save(voyage);

        System.out.println("Balance after finish = " + owner.getBalance());
    }
}