package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VoyageScheduler {

    private final JpaVoyageRepository voyageRepository;
    private final FinishVoyageService finishVoyageService;

    public VoyageScheduler(JpaVoyageRepository voyageRepository,
                           FinishVoyageService finishVoyageService) {
        this.voyageRepository = voyageRepository;
        this.finishVoyageService = finishVoyageService;
    }

    @Scheduled(fixedRate = 10000)
    public void updateVoyages() {

        List<Voyage> voyages = voyageRepository.findByStatus(VoyageStatus.RUNNING);

        for (Voyage voyage : voyages) {

            if (voyage.getStatus() == VoyageStatus.RUNNING &&
                    voyage.getArrivalTime().isBefore(LocalDateTime.now())) {

                finishVoyageService.finishVoyage(voyage.getId());
            }
        }
    }
}