package at.fhv.blueroute.ship.presentation.dto;

public class ShipResponse {

    private Long id;
    private String name;
    private String type;
    private Double price;
    private Integer speed;
    private Long ownerId;
    private Integer condition;
    private Integer fuelLevel;
    private boolean traveling;
    private String currentPort;
    private double cargoCapacity;
    private Double sellPrice;
    private double usedCapacity;


    public ShipResponse() {
    }


    public ShipResponse(Long id, String name, String type, Double price, Integer speed, Long ownerId, Integer condition, Integer fuelLevel, String currentPort, boolean traveling, double cargoCapacity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.speed = speed;
        this.ownerId = ownerId;
        this.condition = condition;
        this.fuelLevel = fuelLevel;
        this.currentPort = currentPort;
        this.traveling = traveling;
        this.cargoCapacity = cargoCapacity;
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

    public Double getPrice() {
        return price;
    }

    public Integer getSpeed() {
        return speed;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Integer getCondition() { return condition; }

    public Integer getFuelLevel() { return fuelLevel; }

    public String getCurrentPort() {
        return currentPort;
    }

    public double getCargoCapacity() {
        return cargoCapacity;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setCurrentPort(String currentPort) {
        this.currentPort = currentPort;
    }

    public boolean isTraveling() {
        return traveling;
    }

    public void setTraveling(boolean traveling) {
        this.traveling = traveling;
    }

    public void setCargoCapacity(double cargoCapacity) {
        this.cargoCapacity = cargoCapacity;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

}