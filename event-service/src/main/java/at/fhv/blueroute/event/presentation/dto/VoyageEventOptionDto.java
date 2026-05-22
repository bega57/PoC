package at.fhv.blueroute.event.presentation.dto;

public class VoyageEventOptionDto {

    private String label;
    private String consequence;
    private boolean minigame;

    public VoyageEventOptionDto() {}

    public VoyageEventOptionDto(String label, String consequence) {
        this.label = label;
        this.consequence = consequence;
        this.minigame = false;
    }

    public VoyageEventOptionDto(String label, String consequence, boolean minigame) {
        this.label = label;
        this.consequence = consequence;
        this.minigame = minigame;
    }

    public String getLabel() { return label; }
    public String getConsequence() { return consequence; }
    public boolean isMinigame() { return minigame; }
}