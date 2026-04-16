package at.fhv.blueroute.cargo.domain.model;

public enum RiskLevel {
    LOW(1.0),
    MEDIUM(1.2),
    HIGH(1.5);

    private final double fuelMultiplier;

    RiskLevel(double fuelMultiplier) {
        this.fuelMultiplier = fuelMultiplier;
    }

    public double getFuelMultiplier() {
        return fuelMultiplier;
    }
}