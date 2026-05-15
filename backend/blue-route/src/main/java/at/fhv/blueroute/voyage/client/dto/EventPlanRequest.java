package at.fhv.blueroute.voyage.client.dto;

import at.fhv.blueroute.event.domain.model.VoyageEventType;

public class EventPlanRequest {

    private VoyageEventType eventType;
    private int eventTriggerTick;

    public EventPlanRequest() {
    }

    public EventPlanRequest(
            VoyageEventType eventType,
            int eventTriggerTick
    ) {
        this.eventType = eventType;
        this.eventTriggerTick = eventTriggerTick;
    }

    public VoyageEventType getEventType() {
        return eventType;
    }

    public int getEventTriggerTick() {
        return eventTriggerTick;
    }
}