package at.fhv.blueroute.event.client;

import at.fhv.blueroute.event.client.dto.ResolveVoyageEventRequest;
import at.fhv.blueroute.event.client.dto.ResolveVoyageEventResponse;
import at.fhv.blueroute.event.client.dto.VoyageEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${event.service.url}") String eventServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(eventServiceUrl + "/voyage-events")
                .build();
    }

    public ResolveVoyageEventResponse resolveEvent(Long voyageId, ResolveVoyageEventRequest request) {
        return restClient.post()
                .uri("/{voyageId}/resolve", voyageId)
                .body(request)
                .retrieve()
                .body(ResolveVoyageEventResponse.class);
    }

    public VoyageEventDto getActiveEvent(Long voyageId) {
        try {
            return restClient.get()
                    .uri("/{voyageId}/active", voyageId)
                    .retrieve()
                    .body(VoyageEventDto.class);
        } catch (Exception e) {
            return null;
        }
    }
}