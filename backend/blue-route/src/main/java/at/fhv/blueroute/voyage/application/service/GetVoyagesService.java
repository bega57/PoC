package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import at.fhv.blueroute.voyage.presentation.dto.VoyageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetVoyagesService {

    private final JpaVoyageRepository voyageRepository;
    private final SeaRouteService seaRouteService;

    public GetVoyagesService(JpaVoyageRepository voyageRepository,
                             SeaRouteService seaRouteService) {
        this.voyageRepository = voyageRepository;
        this.seaRouteService = seaRouteService;
    }

    public List<VoyageResponse> getAllVoyages(Session session) {
        return voyageRepository.findBySessionId(session.getId()).stream()
                .map(v -> mapToResponse(v, session))
                .toList();
    }

    private VoyageResponse mapToResponse(Voyage v, Session session) {
        VoyageResponse dto = new VoyageResponse();

        dto.id = v.getId();
        dto.shipId = v.getShipId();
        dto.originPort = v.getOriginPort();
        dto.destinationPort = v.getDestinationPort();
        dto.status = v.getStatus().name();
        dto.reward = v.getReward();

        int duration = v.getDurationInTicks();
        dto.duration = duration;

        if (session != null && v.getStatus() == VoyageStatus.RUNNING) {

            int currentDay = session.getCurrentTick() - v.getStartTick() + 1;
            int safeDay = Math.max(1, Math.min(duration, currentDay));

            dto.currentDay = safeDay;
            dto.progress = duration > 0
                    ? (double) safeDay / duration
                    : 1.0;

        } else {
            dto.currentDay = duration;
            dto.progress = 1.0;
        }

        dto.route = seaRouteService.getRoute(
                v.getOriginPort(),
                v.getDestinationPort()
        );

        return dto;
    }
}