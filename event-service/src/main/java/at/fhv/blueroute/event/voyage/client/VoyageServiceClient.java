package at.fhv.blueroute.event.voyage.client;

import at.fhv.blueroute.event.voyage.client.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class VoyageServiceClient {

    private final RestClient restClient;

    public VoyageServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${travel.service.url}") String travelServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(travelServiceUrl + "/voyages")
                .build();
    }

    public VoyageResponse getVoyage(Long voyageId) {
        return restClient.get()
                .uri("/{id}", voyageId)
                .retrieve()
                .body(VoyageResponse.class);
    }

    public List<VoyageResponse> getVoyagesBySession(Long sessionId, int currentTick) {
        VoyageResponse[] response = restClient.get()
                .uri("?sessionId={sessionId}&currentTick={currentTick}", sessionId, currentTick)
                .retrieve()
                .body(VoyageResponse[].class);
        return response == null ? List.of() : List.of(response);
    }

    public void markEventTriggered(Long voyageId) {
        restClient.post()
                .uri("/{id}/event-triggered", voyageId)
                .retrieve()
                .toBodilessEntity();
    }

    public void markEventResolved(Long voyageId, String resultMessage) {
        restClient.post()
                .uri("/{id}/event-resolved", voyageId)
                .body(new EventResolvedRequest(resultMessage))
                .retrieve()
                .toBodilessEntity();
    }

    public void setEventCost(Long voyageId, double eventCost) {
        restClient.post()
                .uri("/{id}/event-cost", voyageId)
                .body(new EventCostRequest(eventCost))
                .retrieve()
                .toBodilessEntity();
    }

    public void delayVoyage(Long voyageId, int extraDelayTicks, double extraFuelLoss, double extraConditionLoss) {
        restClient.post()
                .uri("/{id}/delay", voyageId)
                .body(new DelayVoyageRequest(extraDelayTicks, extraFuelLoss, extraConditionLoss))
                .retrieve()
                .toBodilessEntity();
    }

    public void reduceReward(Long voyageId, double rewardLossPercent) {
        restClient.post()
                .uri("/{id}/reward-loss", voyageId)
                .body(new RewardLossRequest(rewardLossPercent))
                .retrieve()
                .toBodilessEntity();
    }
}