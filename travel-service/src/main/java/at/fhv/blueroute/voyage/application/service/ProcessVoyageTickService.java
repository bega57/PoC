package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProcessVoyageTickService {

    private final JpaVoyageRepository voyageRepository;
    private final ShipServiceClient shipServiceClient;
    private final FinishVoyageService finishVoyageService;

    public ProcessVoyageTickService(
            JpaVoyageRepository voyageRepository,
            ShipServiceClient shipServiceClient,
            FinishVoyageService finishVoyageService
    ) {
        this.voyageRepository = voyageRepository;
        this.shipServiceClient = shipServiceClient;
        this.finishVoyageService = finishVoyageService;
    }

    public List<Voyage> processTick(Long sessionId, int currentTick) {

        List<Voyage> finishedVoyages = new ArrayList<>();

        List<Voyage> voyages =
                voyageRepository.findBySessionId(sessionId)
                        .stream()
                        .filter(v -> v.getStatus() == VoyageStatus.RUNNING)
                        .toList();

        for (Voyage voyage : voyages) {

            shipServiceClient.updateVoyageProgress(
                    voyage.getShipId(),
                    voyage.getFuelPerTick(),
                    voyage.getConditionPerTick()
            );

            if (currentTick >= voyage.getArrivalTick()) {
                Voyage finishedVoyage =
                        finishVoyageService.finishVoyage(
                                voyage.getId(),
                                currentTick
                        );

                finishedVoyages.add(finishedVoyage);
            }
        }

        return finishedVoyages;
    }
}