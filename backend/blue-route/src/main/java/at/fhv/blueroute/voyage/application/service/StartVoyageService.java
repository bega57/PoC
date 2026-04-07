package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.voyage.application.exception.VoyageException;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
public class StartVoyageService {

    private final JpaVoyageRepository voyageRepository;
    private final JpaShipRepository shipRepository;
    private final JpaCargoRepository cargoRepository;
    private final PlayerRepository playerRepository;

    public StartVoyageService(
            JpaVoyageRepository voyageRepository,
            JpaShipRepository shipRepository,
            JpaCargoRepository cargoRepository,
            PlayerRepository playerRepository
    ) {
        this.voyageRepository = voyageRepository;
        this.shipRepository = shipRepository;
        this.cargoRepository = cargoRepository;
        this.playerRepository = playerRepository;
    }

    public Voyage startVoyage(Long shipId, Long cargoId) {

        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        if (ship.isTraveling()) {
            throw new VoyageException("Ship is already traveling");
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

        if (ship.getCargoCapacity() < cargo.getRequiredCapacity()) {
            throw new VoyageException("Ship does not have enough cargo capacity");
        }

        if (ship.getOwner() == null) {
            throw new VoyageException("Ship owner not found");
        }

        if (ship.getOwner().getBalance() < cargo.getPrice()) {
            throw new VoyageException("Not enough balance to start this voyage");
        }

        ship.getOwner().setBalance(ship.getOwner().getBalance() - cargo.getPrice());
        playerRepository.save(ship.getOwner());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime arrival = now.plusSeconds(15);

        Voyage voyage = new Voyage();
        voyage.setShipId(shipId);
        voyage.setCargoId(cargo.getId());
        voyage.setOriginPort(cargo.getOriginPort().getName());
        voyage.setDestinationPort(cargo.getDestinationPort().getName());
        voyage.setStatus(VoyageStatus.RUNNING);
        voyage.setStartTime(now);
        voyage.setArrivalTime(arrival);
        voyage.setReward(cargo.getReward());
        voyage.setRewardGranted(false);

        ship.setTraveling(true);
        ship.setCurrentPort(null);
        shipRepository.save(ship);

        return voyageRepository.save(voyage);
    }
}