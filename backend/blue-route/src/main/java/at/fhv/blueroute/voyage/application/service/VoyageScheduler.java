package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
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
    private final JpaSessionRepository sessionRepository;

    public VoyageScheduler(JpaVoyageRepository voyageRepository,
                           FinishVoyageService finishVoyageService,
                           JpaSessionRepository sessionRepository) {
        this.voyageRepository = voyageRepository;
        this.finishVoyageService = finishVoyageService;
        this.sessionRepository = sessionRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void updateVoyages() {
        List<Voyage> voyages = voyageRepository.findByStatus(VoyageStatus.RUNNING);

        for (Voyage voyage : voyages) {
            Session session = sessionRepository
                    .findById(voyage.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            if (session.getCurrentTick() >= voyage.getArrivalTick()) {
                finishVoyageService.finishVoyage(voyage.getId());
            }
        }
    }
}