package at.fhv.blueroute.ship.domain.model;

public enum ShipType {
    CHEAP(10, 50, 12000, "SLOW", +2),
    MEDIUM(20, 100, 30000, "NORMAL", 0),
    EXPENSIVE(30, 200, 42000, "FAST", -1);

    private final int speed;
    private final int capacity;
    private final double price;
    private final String speedCategory;
    private final int tickModifier;

    ShipType(int speed, int capacity, double price, String speedCategory, int tickModifier) {
        this.speed = speed;
        this.capacity = capacity;
        this.price = price;
        this.speedCategory = speedCategory;
        this.tickModifier = tickModifier;
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

    public String getSpeedCategory() {
        return speedCategory;
    }

    public int getTickModifier() {
        return tickModifier;
    }
}
