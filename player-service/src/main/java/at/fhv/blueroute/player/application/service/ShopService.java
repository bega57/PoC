package at.fhv.blueroute.player.application.service;

import at.fhv.blueroute.player.application.exception.InsufficientBalanceException;
import at.fhv.blueroute.player.application.exception.PlayerNotFoundException;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.model.PlayerPowerUp;
import at.fhv.blueroute.player.domain.model.PowerUpType;
import at.fhv.blueroute.player.infrastructure.persistence.JpaPlayerPowerUpRepository;
import at.fhv.blueroute.player.infrastructure.persistence.JpaPlayerRepository;
import at.fhv.blueroute.player.presentation.dto.InventoryItemResponse;
import at.fhv.blueroute.player.presentation.dto.PowerUpItemResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private final JpaPlayerRepository playerRepository;
    private final JpaPlayerPowerUpRepository powerUpRepository;

    public ShopService(JpaPlayerRepository playerRepository,
                       JpaPlayerPowerUpRepository powerUpRepository) {
        this.playerRepository = playerRepository;
        this.powerUpRepository = powerUpRepository;
    }

    public List<PowerUpItemResponse> getShopItems() {
        return Arrays.stream(PowerUpType.values())
                .map(p -> new PowerUpItemResponse(
                        p.name(),
                        p.getDisplayName(),
                        p.getEmoji(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getEffectType()
                ))
                .toList();
    }

    @Transactional
    public List<InventoryItemResponse> buyPowerUp(Long playerId, String powerUpTypeName) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        PowerUpType type;
        try {
            type = PowerUpType.valueOf(powerUpTypeName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown power-up type: " + powerUpTypeName);
        }

        double newBalance = player.getBalance() - type.getPrice();
        if (newBalance < 0) {
            throw new InsufficientBalanceException(playerId);
        }

        player.setBalance(newBalance);
        playerRepository.save(player);

        Optional<PlayerPowerUp> existing = powerUpRepository.findByPlayerIdAndPowerUpType(playerId, type);
        if (existing.isPresent()) {
            PlayerPowerUp item = existing.get();
            item.setQuantity(item.getQuantity() + 1);
            powerUpRepository.save(item);
        } else {
            powerUpRepository.save(new PlayerPowerUp(playerId, type, 1));
        }

        return getInventory(playerId);
    }

    public List<InventoryItemResponse> getInventory(Long playerId) {
        return powerUpRepository.findByPlayerId(playerId)
                .stream()
                .filter(item -> item.getQuantity() > 0)
                .map(item -> new InventoryItemResponse(
                        item.getPowerUpType().name(),
                        item.getPowerUpType().getDisplayName(),
                        item.getPowerUpType().getEmoji(),
                        item.getPowerUpType().getDescription(),
                        item.getPowerUpType().getEffectType(),
                        item.getQuantity()
                ))
                .toList();
    }

    @Transactional
    public String usePowerUp(Long playerId, String powerUpTypeName) {
        PowerUpType type;
        try {
            type = PowerUpType.valueOf(powerUpTypeName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown power-up type: " + powerUpTypeName);
        }

        PlayerPowerUp item = powerUpRepository.findByPlayerIdAndPowerUpType(playerId, type)
                .orElseThrow(() -> new IllegalStateException("Player does not have " + type.getDisplayName()));

        if (item.getQuantity() <= 0) {
            throw new IllegalStateException("No " + type.getDisplayName() + " left in inventory");
        }

        item.setQuantity(item.getQuantity() - 1);
        powerUpRepository.save(item);

        return type.getEffectType();
    }
}
