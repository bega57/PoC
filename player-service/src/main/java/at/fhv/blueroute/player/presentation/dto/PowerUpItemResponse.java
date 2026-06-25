package at.fhv.blueroute.player.presentation.dto;

public class PowerUpItemResponse {

    private String type;
    private String displayName;
    private String emoji;
    private String description;
    private int price;
    private String effectType;

    public PowerUpItemResponse(String type, String displayName, String emoji,
                               String description, int price, String effectType) {
        this.type = type;
        this.displayName = displayName;
        this.emoji = emoji;
        this.description = description;
        this.price = price;
        this.effectType = effectType;
    }

    public String getType() { return type; }
    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public String getEffectType() { return effectType; }
}
