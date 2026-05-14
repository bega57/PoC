package at.fhv.blueroute.port.client.dto;

public record PortResponse(
        Long id,
        String name,
        double latitude,
        double longitude
) {
}