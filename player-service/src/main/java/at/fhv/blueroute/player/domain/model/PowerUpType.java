package at.fhv.blueroute.player.domain.model;

public enum PowerUpType {

    RED_BULL(
            "Red Bull",
            "🐂",
            "Gives your crew wings — voyage arrives 1 day earlier.",
            500,
            "TICK_REDUCTION"
    ),
    CHOCOLATE_CAKE(
            "Chocolate Cake",
            "🍰",
            "Crew morale skyrockets — earn 50% bonus points on next voyage.",
            800,
            "POINTS_BOOST"
    ),
    LUCKY_CLOVER(
            "Lucky Clover",
            "🍀",
            "Fortune smiles on you — customs will look the other way next arrival.",
            1200,
            "CUSTOMS_IMMUNITY"
    ),
    TURBO_CABLE(
            "Turbo Cable",
            "⚡",
            "Override weather delays — event delays are completely ignored next voyage.",
            1500,
            "DELAY_IMMUNITY"
    ),
    VIP_PASS(
            "VIP Pass",
            "💎",
            "Priority docking — earn 20% extra reward on next voyage.",
            2000,
            "REWARD_BOOST"
    ),
    MUSIC_PLAYER(
            "Music Player",
            "🎵",
            "Keep the crew entertained — enjoy some fun music on your next voyage.",
            200,
            "MUSIC"
    );

    private final String displayName;
    private final String emoji;
    private final String description;
    private final int price;
    private final String effectType;

    PowerUpType(String displayName, String emoji, String description, int price, String effectType) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.description = description;
        this.price = price;
        this.effectType = effectType;
    }

    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public String getEffectType() { return effectType; }
}
