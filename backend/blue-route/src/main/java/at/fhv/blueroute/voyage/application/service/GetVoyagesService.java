package at.fhv.blueroute.voyage.application.service;

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

    public List<VoyageResponse> getAllVoyages(Long sessionId, int currentTick) {
        return voyageRepository.findBySessionId(sessionId).stream()
                .map(v -> mapToResponse(v, currentTick))
                .toList();
    }

    private VoyageResponse mapToResponse(Voyage v, int currentTick) {
        VoyageResponse dto = new VoyageResponse();

        dto.id = v.getId();
        dto.shipId = v.getShipId();
        dto.originPort = v.getOriginPort();
        dto.destinationPort = v.getDestinationPort();
        dto.status = v.getStatus().name();
        dto.reward = v.getReward();

        int duration = v.getDurationInTicks();
        dto.duration = duration;

        if (v.getStatus() == VoyageStatus.RUNNING) {

            int elapsedTicks = currentTick - v.getStartTick();
            elapsedTicks = Math.max(0, elapsedTicks);

            dto.currentDay = Math.min(duration, elapsedTicks + 1);

            dto.progress = duration > 0
                    ? Math.min(1.0, (double) (elapsedTicks + 1) / duration)
                    : 1.0;

            System.out.println("DEBUG → startTick=" + v.getStartTick()
                    + " currentTick=" + currentTick
                    + " elapsed=" + elapsedTicks);

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