package at.fhv.blueroute.ship.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ship_cargo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ship_id", "good_id"}))
public class ShipCargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ship_id")
    private Long shipId;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getGoodId() {
        return goodId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    @Column(name = "good_id")
    private Long goodId;

    private int quantity;

}