package at.fhv.blueroute.event.presentation.dto;

public class VoyageEventOptionDto {

    private String label;
    private String consequence;

    public VoyageEventOptionDto() {}

    public VoyageEventOptionDto(String label, String consequence) {
        this.label = label;
        this.consequence = consequence;
    }

    public String getLabel() { return label; }
    public String getConsequence() { return consequence; }
}