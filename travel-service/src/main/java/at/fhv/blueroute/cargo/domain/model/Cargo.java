package at.fhv.blueroute.cargo.domain.model;

import at.fhv.blueroute.port.domain.model.Port;
import jakarta.persistence.*;

@Entity
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoType type;

    @ManyToOne
    @JoinColumn(name = "origin_port")
    private Port originPort;

    @ManyToOne
    @JoinColumn(name = "destination_port")
    private Port destinationPort;

    private double price;

    private double reward;

    private int requiredCapacity;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private int requiredTicks;

    private double fuelConsumption;
    private double conditionDamage;

    @Column
    private String description;

    public Cargo() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CargoType getType() {
        return type;
    }

    public Port getOriginPort() {
        return originPort;
    }

    public Port getDestinationPort() {
        return destinationPort;
    }

    public double getPrice() {
        return price;
    }

    public double getReward() {
        return reward;
    }

    public int getRequiredCapacity() {
        return requiredCapacity;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public double getConditionDamage() {
        return conditionDamage;
    }

    public int getRequiredTicks() {
        return requiredTicks;
    }

    public String getDescription() { return description; }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Cargo name must not be empty.");
        }
        this.name = name;
    }

    public void setType(CargoType type) {
        if (type == null) {
            throw new IllegalArgumentException("Cargo type must not be null.");
        }
        this.type = type;
    }

    public void setOriginPort(Port originPort) {
        this.originPort = originPort;
    }

    public void setDestinationPort(Port destinationPort) {
        this.destinationPort = destinationPort;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public void setRequiredCapacity(int requiredCapacity) {
        this.requiredCapacity = requiredCapacity;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        if (riskLevel == null) {
            throw new IllegalArgumentException("Risk level must not be null.");
        }
        this.riskLevel = riskLevel;
    }

    public void setRequiredTicks(int requiredTicks) {
        this.requiredTicks = requiredTicks;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public void setConditionDamage(double conditionDamage) {
        this.conditionDamage = conditionDamage;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}