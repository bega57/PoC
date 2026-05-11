package at.fhv.blueroute.voyage.application.service;

import at.fhv.blueroute.voyage.domain.model.Voyage;
import at.fhv.blueroute.voyage.domain.model.VoyageStatus;
import at.fhv.blueroute.voyage.infrastructure.persistence.JpaVoyageRepository;
import at.fhv.blueroute.voyage.presentation.dto.VoyageResponse;
import at.fhv.blueroute.ship.client.ShipServiceClient;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetVoyagesService {

    private final JpaVoyageRepository voyageRepository;
    private final SeaRouteService seaRouteService;
    private final ShipServiceClient shipServiceClient;

    public GetVoyagesService(JpaVoyageRepository voyageRepository,
                             SeaRouteService seaRouteService, ShipServiceClient shipServiceClient) {
        this.voyageRepository = voyageRepository;
        this.seaRouteService = seaRouteService;
        this.shipServiceClient = shipServiceClient;
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
        try {

            ShipResponse ship =
                    shipServiceClient.getShip(v.getShipId());

            dto.shipName = ship.getName();

        } catch (Exception e) {

            dto.shipName = "Unknown Ship";
        }
        dto.originPort = v.getOriginPort();
        dto.destinationPort = v.getDestinationPort();
        dto.status = v.getStatus().name();
        dto.reward = v.getReward();

        dto.eventResultMessage = v.getEventResultMessage();
        dto.extraDelayTicks = v.getExtraDelayTicks();
        dto.extraFuelLoss = v.getExtraFuelLoss();
        dto.extraConditionLoss = v.getExtraConditionLoss();
        dto.eventCost = v.getEventCost();
        dto.rewardLossPercent = v.getRewardLossPercent();

        int durationTicks = v.getDurationInTicks();
        dto.duration = durationTicks;

        if (v.getStatus() == VoyageStatus.RUNNING) {

            long durationMs = durationTicks * 5000L;

            long startMs = v.getStartTime()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            long now = System.currentTimeMillis();

            double progress = (double) (now - startMs) / durationMs;

            dto.progress = Math.min(1.0, Math.max(0.0, progress));

            dto.currentDay = (int) Math.ceil(dto.progress * durationTicks);

        } else {
            dto.currentDay = durationTicks;
            dto.progress = 1.0;
        }

        dto.route = seaRouteService.getRoute(
                v.getOriginPort(),
                v.getDestinationPort()
        );

        return dto;
    }
}