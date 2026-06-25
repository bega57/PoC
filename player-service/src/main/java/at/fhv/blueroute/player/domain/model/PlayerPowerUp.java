package at.fhv.blueroute.player.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "player_power_up")
public class PlayerPowerUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long playerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PowerUpType powerUpType;

    @Column(nullable = false)
    private int quantity = 0;

    public PlayerPowerUp() {}

    public PlayerPowerUp(Long playerId, PowerUpType powerUpType, int quantity) {
        this.playerId = playerId;
        this.powerUpType = powerUpType;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public PowerUpType getPowerUpType() { return powerUpType; }
    public void setPowerUpType(PowerUpType powerUpType) { this.powerUpType = powerUpType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
