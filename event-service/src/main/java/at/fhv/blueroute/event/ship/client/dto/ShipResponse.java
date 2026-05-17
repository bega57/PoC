package at.fhv.blueroute.event.ship.client.dto;

public class ShipResponse {

    private Long id;
    private Long ownerId;
    private Integer condition;
    private Integer fuelLevel;

    public ShipResponse() {}

    public Long getId() { return id; }
    public Long getOwnerId() { return ownerId; }
    public Integer getCondition() { return condition; }
    public Integer getFuelLevel() { return fuelLevel; }
}