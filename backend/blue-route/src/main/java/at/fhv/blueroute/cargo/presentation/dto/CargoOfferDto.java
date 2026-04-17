package at.fhv.blueroute.cargo.presentation.dto;

public record CargoOfferDto(
        Long id,
        String name,
        String fromPort,
        String toPort,
        double reward,
        String riskLevel
) {
}