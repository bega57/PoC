package at.fhv.blueroute.ship.domain.model;

import at.fhv.blueroute.player.domain.model.Player;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ShipType type;

    private Double price;


    @Column(name = "cargo_capacity")
    private double cargoCapacity;

    private Integer speed;

    private Double condition;
    private Double fuelLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Player owner;

    private String currentPort;

    private boolean traveling;


    private double usedCapacity = 0;

    public Ship() {
    }

    public Ship(String name, ShipType type, Double price, Integer speed, Player owner) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.speed = speed;
        this.owner = owner;

        this.condition = 100.0;
        this.fuelLevel = 100.0;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ShipType getType() {
        return type;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getSpeed() {
        return speed;
    }

    public Player getOwner() {
        return owner;
    }

    public double getCargoCapacity() { return cargoCapacity;}

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ShipType type) {
        this.type = type;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Double getCondition() {
        return condition;
    }

    public void setCondition(Double condition) {
        this.condition = condition;
    }

    public Double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public String getCurrentPort() { return currentPort; }

    public void setCurrentPort(String currentPort) { this.currentPort = currentPort; }

    public boolean isTraveling() { return traveling; }

    public void setTraveling(boolean traveling) { this.traveling = traveling; }

    public void setCargoCapacity(double cargoCapacity) { this.cargoCapacity = cargoCapacity;}

    public double getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(double usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

}