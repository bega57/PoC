package at.fhv.blueroute.session.event.client;

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

    public boolean processTick(Long sessionId, String sessionCode, int currentTick) {
        Boolean result = restClient.post()
                .uri("/process-tick?sessionId={sessionId}&sessionCode={sessionCode}&currentTick={currentTick}",
                        sessionId, sessionCode, currentTick)
                .retrieve()
                .body(Boolean.class);
        return Boolean.TRUE.equals(result);
    }
}