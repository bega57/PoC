package at.fhv.blueroute.voyage.client;

import at.fhv.blueroute.voyage.client.dto.StartVoyageRequest;
import at.fhv.blueroute.voyage.client.dto.VoyageResponse;
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

    // ==================== SMUGGLING RESOLVE ====================
    public VoyageResponse resolveSmuggling(Long voyageId, boolean bribe) {

        String url =
                travelServiceUrl
                        + "/voyages/"
                        + voyageId
                        + "/smuggling-resolve?bribe="
                        + bribe;

        return restTemplate.postForObject(
                url,
                null,
                VoyageResponse.class
        );
    }
    // ===========================================================
}
