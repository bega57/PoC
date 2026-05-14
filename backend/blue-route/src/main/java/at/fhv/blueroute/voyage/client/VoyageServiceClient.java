package at.fhv.blueroute.voyage.client;

import at.fhv.blueroute.event.domain.model.VoyageEventType;
import at.fhv.blueroute.voyage.client.dto.StartVoyageRequest;
import at.fhv.blueroute.voyage.client.dto.VoyageResponse;
import at.fhv.blueroute.voyage.client.dto.DelayVoyageRequest;
import at.fhv.blueroute.voyage.client.dto.EventCostRequest;
import at.fhv.blueroute.voyage.client.dto.EventResolvedRequest;
import at.fhv.blueroute.voyage.client.dto.RewardLossRequest;
import at.fhv.blueroute.voyage.client.dto.EventPlanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class VoyageServiceClient {

    private final RestTemplate restTemplate;

    @Value("${travel.service.url}")
    private String travelServiceUrl;

    public VoyageServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    public VoyageResponse startVoyage(
            StartVoyageRequest request
    ) {

        String url =
                travelServiceUrl + "/voyages/start";

        return restTemplate.postForObject(
                url,
                request,
                VoyageResponse.class
        );
    }

    public List<VoyageResponse> getVoyages(
            Long sessionId,
            int currentTick
    ) {

        String url =
                travelServiceUrl
                        + "/voyages?sessionId="
                        + sessionId
                        + "&currentTick="
                        + currentTick;

        VoyageResponse[] response =
                restTemplate.getForObject(
                        url,
                        VoyageResponse[].class
                );

        return response == null
                ? List.of()
                : List.of(response);
    }


    public VoyageResponse finishVoyage(Long voyageId, int currentTick) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/finish?currentTick="
                        + currentTick;

        return restTemplate.postForObject(
                url,
                null,
                VoyageResponse.class
        );
    }

    public List<VoyageResponse> processTick(Long sessionId, int currentTick) {

        String url =
                travelServiceUrl
                        + "/voyages/process-tick?sessionId="
                        + sessionId
                        + "&currentTick="
                        + currentTick;

        VoyageResponse[] response =
                restTemplate.postForObject(
                        url,
                        null,
                        VoyageResponse[].class
                );

        return response == null
                ? List.of()
                : List.of(response);
    }

    public VoyageResponse getVoyage(Long voyageId) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId;

        return restTemplate.getForObject(
                url,
                VoyageResponse.class
        );
    }

    public void markEventResolved(Long voyageId, String resultMessage) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/event-resolved";

        restTemplate.postForObject(
                url,
                new EventResolvedRequest(resultMessage),
                Void.class
        );
    }

    public void setEventCost(Long voyageId, double eventCost) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/event-cost";

        restTemplate.postForObject(
                url,
                new EventCostRequest(eventCost),
                Void.class
        );
    }

    public void delayVoyage(
            Long voyageId,
            int extraDelayTicks,
            double extraFuelLoss,
            double extraConditionLoss
    ) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/delay";

        restTemplate.postForObject(
                url,
                new DelayVoyageRequest(
                        extraDelayTicks,
                        extraFuelLoss,
                        extraConditionLoss
                ),
                Void.class
        );
    }

    public void reduceReward(Long voyageId, double rewardLossPercent) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/reward-loss";

        restTemplate.postForObject(
                url,
                new RewardLossRequest(rewardLossPercent),
                Void.class
        );
    }

    public void markEventTriggered(Long voyageId) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/event-triggered";

        restTemplate.postForObject(
                url,
                null,
                Void.class
        );
    }

    public void assignEventToVoyage(
            Long voyageId,
            VoyageEventType eventType,
            int eventTriggerTick
    ) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/event-plan";

        restTemplate.postForObject(
                url,
                new EventPlanRequest(
                        eventType,
                        eventTriggerTick
                ),
                Void.class
        );
    }
}