package at.fhv.blueroute.event.application.dto;

import at.fhv.blueroute.event.domain.model.VoyageEventType;

public class PlannedVoyageEvent {

    private VoyageEventType eventType;
    private int eventTriggerTick;

    public PlannedVoyageEvent(VoyageEventType eventType, int eventTriggerTick) {
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