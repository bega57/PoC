package at.fhv.blueroute.ship.presentation.dto;

public class ShipResponse {

    private Long id;
    private String name;
    private String type;
    private Double price;
    private Integer capacity;
    private Integer speed;
    private Long ownerId;
    private Integer condition;
    private Integer fuelLevel;
    private boolean traveling;
    private String currentPort;

    public ShipResponse() {
    }

    public ShipResponse(Long id, String name, String type, Double price, Integer capacity, Integer speed, Long ownerId, Integer condition, Integer fuelLevel, String currentPort, boolean traveling) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.capacity = capacity;
        this.speed = speed;
        this.ownerId = ownerId;
        this.condition = condition;
        this.fuelLevel = fuelLevel;
        this.currentPort = currentPort;
        this.traveling = traveling;
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

    public Integer getCapacity() {
        return capacity;
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

    public void setCurrentPort(String currentPort) {
        this.currentPort = currentPort;
    }

    public boolean isTraveling() {
        return traveling;
    }

    public void setTraveling(boolean traveling) {
        this.traveling = traveling;
    }
}