package at.fhv.blueroute.player.client.dto;

public class InventoryItemResponse {

    private String type;
    private String displayName;
    private String emoji;
    private String description;
    private String effectType;
    private int quantity;

    public InventoryItemResponse() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEffectType() { return effectType; }
    public void setEffectType(String effectType) { this.effectType = effectType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
