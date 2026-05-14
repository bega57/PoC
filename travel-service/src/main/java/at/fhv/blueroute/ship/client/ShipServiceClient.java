package at.fhv.blueroute.ship.client;

import at.fhv.blueroute.ship.client.dto.FinishVoyageRequest;
import at.fhv.blueroute.ship.client.dto.ShipResponse;
import at.fhv.blueroute.ship.client.dto.StartVoyageRequest;
import org.springframework.beans.factory.annotation.Value;
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

        String url =
                shipServiceUrl
                        + "/ships/"
                        + shipId
                        + "/voyage-progress"
                        + "?fuelLoss="
                        + fuelLoss
                        + "&conditionLoss="
                        + conditionLoss;

        restTemplate.put(
                url,
                null
        );
    }
}