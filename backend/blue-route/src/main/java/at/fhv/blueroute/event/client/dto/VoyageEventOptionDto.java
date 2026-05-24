package at.fhv.blueroute.event.client.dto;

public class VoyageEventOptionDto {

    private String label;
    private String consequence;
    private boolean minigame;

    public VoyageEventOptionDto() {}

    public String getLabel() { return label; }
    public String getConsequence() { return consequence; }
    public boolean isMinigame() { return minigame; }
}