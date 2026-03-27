package at.fhv.blueroute.voyage.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Voyage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipId;

    private String originPort;

    private String destinationPort;

    @Enumerated(EnumType.STRING)
    private VoyageStatus status;

    private LocalDateTime startTime;
        private LocalDateTime arrivalTime;


    public Long getId() {
        return id;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public String getOriginPort() {
        return originPort;
    }

    public void setOriginPort(String originPort) {
        this.originPort = originPort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public VoyageStatus getStatus() {
        return status;
    }

    public void setStatus(VoyageStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}