package at.fhv.blueroute.voyage.presentation.dto;

import at.fhv.blueroute.voyage.domain.model.VoyageEventType;

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