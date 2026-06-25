package at.fhv.blueroute.player.presentation.dto;

public class InventoryItemResponse {

    private String type;
    private String displayName;
    private String emoji;
    private String description;
    private String effectType;
    private int quantity;

    public InventoryItemResponse(String type, String displayName, String emoji,
                                 String description, String effectType, int quantity) {
        this.type = type;
        this.displayName = displayName;
        this.emoji = emoji;
        this.description = description;
        this.effectType = effectType;
        this.quantity = quantity;
    }

    public String getType() { return type; }
    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
    public String getEffectType() { return effectType; }
    public int getQuantity() { return quantity; }
}
