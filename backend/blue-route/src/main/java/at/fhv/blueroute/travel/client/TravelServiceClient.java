package at.fhv.blueroute.travel.client;

import at.fhv.blueroute.travel.client.dto.StartVoyageRequest;
import at.fhv.blueroute.travel.client.dto.VoyageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class TravelServiceClient {

    private final RestTemplate restTemplate;

    @Value("${travel.service.url}")
    private String travelServiceUrl;

    public TravelServiceClient() {
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


    public void finishVoyage(Long voyageId, int currentTick) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/finish?currentTick="
                        + currentTick;

        restTemplate.postForObject(
                url,
                null,
                Void.class
        );
    }

    public boolean hasPendingEvents(Long sessionId) {

        String url =
                travelServiceUrl
                        + "/voyages/pending-events?sessionId="
                        + sessionId;

        Boolean response =
                restTemplate.getForObject(
                        url,
                        Boolean.class
                );

        return response != null && response;
    }

    public void processTick() {

        restTemplate.postForObject(
                travelServiceUrl + "/voyages/process-tick",
                null,
                Void.class
        );
    }
}