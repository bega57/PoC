package at.fhv.blueroute.event.presentation.dto;

import at.fhv.blueroute.event.domain.model.VoyageEventType;

import java.util.List;

public class VoyageEventDto {

    private Long voyageId;
    private VoyageEventType eventType;
    private String title;
    private String description;
    private List<String> options;

    public VoyageEventDto() {
    }

    public VoyageEventDto(Long voyageId, VoyageEventType eventType, String title, String description, List<String> options) {
        this.voyageId = voyageId;
        this.eventType = eventType;
        this.title = title;
        this.description = description;
        this.options = options;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }

    public VoyageEventType getEventType() {
        return eventType;
    }

    public void setEventType(VoyageEventType eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}