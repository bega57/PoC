package at.fhv.blueroute.port.client;

import at.fhv.blueroute.port.client.dto.PortResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class PortServiceClient {

    private final RestClient restClient;

    public PortServiceClient(
            RestClient.Builder builder,
            @Value("${travel.service.url}") String travelServiceUrl
    ) {
        this.restClient = builder.baseUrl(travelServiceUrl).build();
    }

    public List<PortResponse> getAllPorts() {
        return restClient.get()
                .uri("/ports")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}