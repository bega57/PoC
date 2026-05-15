package at.fhv.blueroute.cargo.presentation.dto;

public record CargoOfferDto(
        Long id,
        String name,
        String type,
        String fromPort,
        String toPort,
        double price,
        double reward,
        int requiredCapacity,
        int requiredTicks,
        double fuelConsumption,
        double conditionDamage,
        String riskLevel
) {
}