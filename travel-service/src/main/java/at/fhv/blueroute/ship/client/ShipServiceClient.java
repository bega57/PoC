package at.fhv.blueroute.ship.client;

import at.fhv.blueroute.ship.client.dto.FinishVoyageRequest;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import at.fhv.blueroute.ship.client.dto.StartVoyageRequest;
import at.fhv.blueroute.ship.client.dto.VoyageProgressRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class ShipServiceClient {

    private final RestTemplate restTemplate;

    @Value("${ship.service.url}")
    private String shipServiceUrl;

    public ShipServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    public ShipResponse getShip(Long shipId) {

        String url =
                shipServiceUrl
                        + "/ships/"
                        + shipId;

        return restTemplate.getForObject(
                url,
                ShipResponse.class
        );
    }

    public void startVoyage(
            Long shipId,
            double usedCapacity
    ) {

        String url =
                shipServiceUrl
                        + "/ships/"
                        + shipId
                        + "/start-voyage";

        StartVoyageRequest request =
                new StartVoyageRequest(
                        usedCapacity
                );

        restTemplate.postForObject(
                url,
                request,
                Void.class
        );
    }

    public void finishVoyage(
            Long shipId,
            String destinationPort,
            double releasedCapacity
    ) {

        String url =
                shipServiceUrl
                        + "/ships/"
                        + shipId
                        + "/finish-voyage";

        FinishVoyageRequest request =
                new FinishVoyageRequest(
                        destinationPort,
                        releasedCapacity
                );

        restTemplate.postForObject(
                url,
                request,
                Void.class
        );
    }

    public void updateVoyageProgress(
            Long shipId,
            double fuelLoss,
            double conditionLoss
    ) {

        VoyageProgressRequest request =
                new VoyageProgressRequest();

        request.setFuelLoss(fuelLoss);
        request.setConditionLoss(conditionLoss);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<VoyageProgressRequest> entity =
                new HttpEntity<>(request, headers);

        restTemplate.exchange(
                shipServiceUrl + "/ships/" + shipId + "/voyage-progress",
                HttpMethod.PUT,
                entity,
                Void.class
        );
    }
}