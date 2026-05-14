package at.fhv.blueroute.cargo.domain.model;

public enum RiskLevel {
    LOW(1.2),
    MEDIUM(1.5),
    HIGH(2.0);

    private final double fuelMultiplier;

    RiskLevel(double fuelMultiplier) {
        this.fuelMultiplier = fuelMultiplier;
    }

    public double getFuelMultiplier() {
        return fuelMultiplier;
    }
}