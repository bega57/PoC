package at.fhv.blueroute.travel.presentation.dto;

import at.fhv.blueroute.travel.domain.model.VoyageEventType;

public class EventPlanRequest {

    private VoyageEventType eventType;
    private int eventTriggerTick;

    public EventPlanRequest() {
    }

    public VoyageEventType getEventType() {
        return eventType;
    }

    public int getEventTriggerTick() {
        return eventTriggerTick;
    }
}