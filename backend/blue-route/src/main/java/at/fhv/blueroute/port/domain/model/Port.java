package at.fhv.blueroute.port.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Port {

    @Id
    private String name;

    private double latitude;
    private double longitude;
    private double fuelPrice;

    public Port() {}

    public Port(String name, double latitude, double longitude, double fuelPrice) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fuelPrice = fuelPrice;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getFuelPrice() {
        return fuelPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setFuelPrice(double fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

}