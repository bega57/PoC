package at.fhv.blueroute.cargo.client;

import at.fhv.blueroute.cargo.client.dto.CargoOfferDto;
import at.fhv.blueroute.cargo.client.dto.CargoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class CargoServiceClient {

    private final RestTemplate restTemplate;

    @Value("${travel.service.url}")
    private String travelServiceUrl;

    public CargoServiceClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<CargoResponse> getCargo(String portName) {

        String url =
                travelServiceUrl + "/cargo?portName=" + portName;

        CargoResponse[] response =
                restTemplate.getForObject(
                        url,
                        CargoResponse[].class
                );

        return response == null
                ? List.of()
                : List.of(response);
    }

    public List<CargoOfferDto> getCargoOffers() {

        String url =
                travelServiceUrl + "/cargo/offers";

        CargoOfferDto[] response =
                restTemplate.getForObject(
                        url,
                        CargoOfferDto[].class
                );

        return response == null
                ? List.of()
                : List.of(response);
    }
}