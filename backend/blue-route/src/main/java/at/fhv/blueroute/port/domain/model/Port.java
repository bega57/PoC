package at.fhv.blueroute.port.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Port {

    @Id
    private String name;

    private double latitude;
    private double longitude;

    public Port() {}

    public Port(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}