package at.fhv.blueroute.cargo.domain.model;

import at.fhv.blueroute.port.domain.model.Port;
import jakarta.persistence.*;

@Entity
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_port")
    private Port originPort;

    @ManyToOne
    @JoinColumn(name = "destination_port")
    private Port destinationPort;

    private double price;

    public Cargo() {}

    public Long getId() { return id; }
    public Port getOriginPort() { return originPort; }
    public Port getDestinationPort() { return destinationPort; }
    public double getPrice() { return price; }

    public void setOriginPort(Port originPort) { this.originPort = originPort; }
    public void setDestinationPort(Port destinationPort) { this.destinationPort = destinationPort; }
    public void setPrice(double price) { this.price = price; }
}