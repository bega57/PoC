package at.fhv.blueroute.voyage.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Voyage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipId;

    private Long cargoId;

    private String originPort;

    private String destinationPort;

    @Enumerated(EnumType.STRING)
    private VoyageStatus status;

    private LocalDateTime startTime;
    private LocalDateTime arrivalTime;

    private double reward;

    private boolean rewardGranted;

    @Column(nullable = false)
    private int startTick;

    @Column(nullable = false)
    private int arrivalTick;

    @Column(nullable = false)
    private Long sessionId;

    @Transient
    public int getDurationInTicks() {
        return arrivalTick - startTick;
    }


    public Long getId() {
        return id;
    }

    public Long getShipId() {
        return shipId;
    }

    public void setShipId(Long shipId) {
        this.shipId = shipId;
    }

    public Long getCargoId() {
        return cargoId;
    }

    public void setCargoId(Long cargoId) {
        this.cargoId = cargoId;
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

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public boolean isRewardGranted() {
        return rewardGranted;
    }

    public void setRewardGranted(boolean rewardGranted) {
        this.rewardGranted = rewardGranted;
    }


    public int getArrivalTick() {
        return arrivalTick;
    }

    public void setArrivalTick(int arrivalTick) {
        this.arrivalTick = arrivalTick;
    }

    public int getStartTick() {
        return startTick;
    }

    public void setStartTick(int startTick) {
        this.startTick = startTick;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }



}