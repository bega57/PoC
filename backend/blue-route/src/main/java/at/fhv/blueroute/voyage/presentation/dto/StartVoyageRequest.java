package at.fhv.blueroute.voyage.presentation.dto;

public class StartVoyageRequest {

    private Long shipId;
    private String originPort;
    private String destinationPort;

    public Long getShipId() {
        return shipId;
    }

    public String getOriginPort() {
        return originPort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }
}