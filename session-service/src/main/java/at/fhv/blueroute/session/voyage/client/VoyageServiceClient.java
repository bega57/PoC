package at.fhv.blueroute.session.voyage.client;

import at.fhv.blueroute.session.voyage.client.dto.VoyageResponse;
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

    public List<VoyageResponse> processTick(Long sessionId, int currentTick) {
        VoyageResponse[] response = restClient.post()
                .uri("/process-tick?sessionId={sessionId}&currentTick={currentTick}", sessionId, currentTick)
                .retrieve()
                .body(VoyageResponse[].class);

        return response == null ? List.of() : List.of(response);
    }

    public List<VoyageResponse> getVoyages(Long sessionId, int currentTick) {
        VoyageResponse[] response = restClient.get()
                .uri("?sessionId={sessionId}&currentTick={currentTick}", sessionId, currentTick)
                .retrieve()
                .body(VoyageResponse[].class);

        return response == null ? List.of() : List.of(response);
    }
}