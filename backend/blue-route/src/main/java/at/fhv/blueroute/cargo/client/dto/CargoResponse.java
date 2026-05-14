package at.fhv.blueroute.cargo.client.dto;

public class CargoResponse {

    private Long id;
    private String name;
    private String type;
    private double price;
    private double reward;
    private int requiredCapacity;
    private String riskLevel;
    private int requiredTicks;
    private double fuelConsumption;
    private double conditionDamage;
    private String description;

    public PortDto getOriginPort() {
        return originPort;
    }

    public void setOriginPort(PortDto originPort) {
        this.originPort = originPort;
    }

    public PortDto getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(PortDto destinationPort) {
        this.destinationPort = destinationPort;
    }

    private PortDto originPort;
    private PortDto destinationPort;

    public CargoResponse() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
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

    public String getRiskLevel() {
        return riskLevel;
    }

    public int getRequiredTicks() {
        return requiredTicks;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public double getConditionDamage() {
        return conditionDamage;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setRiskLevel(String riskLevel) {
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