package at.fhv.blueroute.travel.client.dto;

public class EventCostRequest {

    private double eventCost;

    public EventCostRequest() {
    }

    public EventCostRequest(double eventCost) {
        this.eventCost = eventCost;
    }

    public double getEventCost() {
        return eventCost;
    }
}