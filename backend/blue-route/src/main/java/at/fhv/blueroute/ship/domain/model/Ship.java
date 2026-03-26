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

    private Integer capacity;

    private Integer speed;

    private Integer condition;

    private Integer fuelLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Player owner;

    public Ship() {
    }

    public Ship(String name, ShipType type, Double price, Integer capacity, Integer speed, Player owner) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.capacity = capacity;
        this.speed = speed;
        this.owner = owner;

        this.condition = 100;
        this.fuelLevel = 100;
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

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getSpeed() {
        return speed;
    }

    public Player getOwner() {
        return owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ShipType type) {
        this.type = type;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Integer getCondition() {
        return condition;
    }

    public void setCondition(Integer condition) {
        this.condition = condition;
    }

    public Integer getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Integer fuelLevel) {
        this.fuelLevel = fuelLevel;
    }
}