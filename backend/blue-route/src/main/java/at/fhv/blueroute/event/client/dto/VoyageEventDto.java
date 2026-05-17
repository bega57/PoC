package at.fhv.blueroute.event.client.dto;

import java.util.List;

public class VoyageEventDto {

    private String type;
    private Long voyageId;
    private String eventType;
    private String title;
    private String description;
    private List<VoyageEventOptionDto> options;

    public VoyageEventDto() {}

    public String getType() { return type; }
    public Long getVoyageId() { return voyageId; }
    public String getEventType() { return eventType; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<VoyageEventOptionDto> getOptions() { return options; }
}