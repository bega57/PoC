package at.fhv.blueroute.ship.client;

import at.fhv.blueroute.ship.client.dto.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ShipServiceClient {

    private final RestTemplate restTemplate;

    @Value("${ship.service.url}")
    private String shipServiceUrl;

    public ShipServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<ShipResponse> getShipsByPlayer(
            Long playerId
    ) {

        String url =
                shipServiceUrl
                        + "/ships/player/"
                        + playerId;

        ShipResponse[] response =
                restTemplate.getForObject(
                        url,
                        ShipResponse[].class
                );

        return response == null
                ? List.of()
                : List.of(response);
    }

    public ShipResponse buyShip(
            BuyShipRequest request
    ) {
        try {
            return restTemplate.postForObject(shipServiceUrl + "/ships/buy", request, ShipResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException(extractMessage(ex.getResponseBodyAsString(), "Purchase failed."), ex);
        } catch (RestClientException ex) {
            throw new RuntimeException("Something went wrong. Please try again.", ex);
        }
    }

    public ShipResponse sellShip(
            SellShipRequest request
    ) {
        try {
            return restTemplate.postForObject(shipServiceUrl + "/ships/sell", request, ShipResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException(extractMessage(ex.getResponseBodyAsString(), "Sale failed."), ex);
        } catch (RestClientException ex) {
            throw new RuntimeException("Something went wrong. Please try again.", ex);
        }
    }

    private String extractMessage(String body, String fallback) {
        Matcher m = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        return m.find() ? m.group(1) : fallback;
    }

    public List<UsedShipOfferResponse> getUsedShips() {

        String url =
                shipServiceUrl + "/ships/used";

        UsedShipOfferResponse[] response =
                restTemplate.getForObject(
                        url,
                        UsedShipOfferResponse[].class
                );

        return response == null
                ? List.of()
                : List.of(response);
    }

    public ShipResponse buyUsedShip(
            Long offerId,
            BuyUsedShipRequest request
    ) {
        String url = shipServiceUrl + "/ships/used/" + offerId + "/buy";
        try {
            return restTemplate.postForObject(url, request, ShipResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException(extractMessage(ex.getResponseBodyAsString(), "Purchase failed."), ex);
        } catch (RestClientException ex) {
            throw new RuntimeException("Something went wrong. Please try again.", ex);
        }
    }

    public ShipResponse repairShip(
            Long shipId,
            RepairShipRequest request
    ) {

        String url =
                shipServiceUrl
                        + "/ships/"
                        + shipId
                        + "/repair";

        return restTemplate.postForObject(
                url,
                request,
                ShipResponse.class
        );
    }

    public ShipResponse refuelShip(
            Long shipId,
            RefuelShipRequest request
    ) {

        String url =
                shipServiceUrl
                        + "/ships/"
                        + shipId
                        + "/refuel";

        return restTemplate.postForObject(
                url,
                request,
                ShipResponse.class
        );
    }

    public double getRepairCost(
            Long shipId,
            int repairAmount
    ) {

        String url =
                shipServiceUrl
                        + "/ships/"
                        + shipId
                        + "/repair-cost?repairAmount="
                        + repairAmount;

        Double response =
                restTemplate.getForObject(
                        url,
                        Double.class
                );

        return response == null
                ? 0
                : response;
    }

    public double getRefuelCost(
            Long shipId,
            int fuelAmount
    ) {

        String url =
                shipServiceUrl
                        + "/ships/"
                        + shipId
                        + "/refuel-cost?fuelAmount="
                        + fuelAmount;

        Double response =
                restTemplate.getForObject(
                        url,
                        Double.class
                );

        return response == null
                ? 0
                : response;
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

    public List<ShipResponse> getAllShips() {

        String url =
                shipServiceUrl + "/ships";

        ShipResponse[] response =
                restTemplate.getForObject(
                        url,
                        ShipResponse[].class
                );

        return response == null
                ? List.of()
                : List.of(response);
    }

    public void assignPortToUnlocatedShips(Long playerId, String port) {

        String url =
                shipServiceUrl
                        + "/ships/player/"
                        + playerId
                        + "/assign-port?port="
                        + port;

        restTemplate.postForObject(url, null, Void.class);
    }
}