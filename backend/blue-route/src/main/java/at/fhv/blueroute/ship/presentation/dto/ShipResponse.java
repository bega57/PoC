package at.fhv.blueroute.ship.presentation.dto;

public class ShipResponse {

    private Long id;
    private String name;
    private String type;
    private Double price;
    private Integer capacity;
    private Integer speed;
    private Long ownerId;

    public ShipResponse() {
    }

    public ShipResponse(Long id, String name, String type, Double price, Integer capacity, Integer speed, Long ownerId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.capacity = capacity;
        this.speed = speed;
        this.ownerId = ownerId;
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
}