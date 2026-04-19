package at.fhv.blueroute.ship.domain.model;

public enum ShipType {
    CHEAP(10, 50, 12000),
    MEDIUM(20, 100, 30000),
    EXPENSIVE(30, 200, 60000);

    private final int speed;
    private final int capacity;
    private final double price;

    ShipType(int speed, int capacity, double price) {
        this.speed = speed;
        this.capacity = capacity;
        this.price = price;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPrice() {
        return price;
    }
}