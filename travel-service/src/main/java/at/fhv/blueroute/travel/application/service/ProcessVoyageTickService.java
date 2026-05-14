package at.fhv.blueroute.travel.application.service;

import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.travel.domain.model.Voyage;
import at.fhv.blueroute.travel.domain.model.VoyageStatus;
import at.fhv.blueroute.travel.infrastructure.persistence.JpaVoyageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProcessVoyageTickService {

    private final JpaVoyageRepository voyageRepository;
    private final ShipServiceClient shipServiceClient;

    public ProcessVoyageTickService(
            JpaVoyageRepository voyageRepository,
            ShipServiceClient shipServiceClient
    ) {
        this.voyageRepository = voyageRepository;
        this.shipServiceClient = shipServiceClient;
    }

    public void processTick() {

        List<Voyage> voyages =
                voyageRepository.findAll()
                        .stream()
                        .filter(v ->
                                v.getStatus() == VoyageStatus.RUNNING
                        )
                        .toList();

        for (Voyage voyage : voyages) {

            shipServiceClient.updateVoyageProgress(
                    voyage.getShipId(),
                    voyage.getFuelPerTick(),
                    voyage.getConditionPerTick()
            );
        }
    }
}