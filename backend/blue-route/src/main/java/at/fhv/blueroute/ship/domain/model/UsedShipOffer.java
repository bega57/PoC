package at.fhv.blueroute.ship.domain.model;

import at.fhv.blueroute.session.domain.model.Session;
import jakarta.persistence.*;

@Entity
public class UsedShipOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ShipType type;

    private double price;

    private double condition;

    private double fuelLevel;

    @ManyToOne
    private Session session;

    protected UsedShipOffer() {
    }

    public UsedShipOffer(ShipType type, double price, double condition, double fuelLevel, Session session) {
        this.type = type;
        this.price = price;
        this.condition = condition;
        this.fuelLevel = fuelLevel;
        this.session = session;
    }

    public Long getId() {
        return id;
    }

    public ShipType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public double getCondition() {
        return condition;
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public Session getSession() {
        return session;
    }
}