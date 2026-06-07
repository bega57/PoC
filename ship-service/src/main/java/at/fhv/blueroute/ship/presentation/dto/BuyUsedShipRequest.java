package at.fhv.blueroute.ship.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BuyUsedShipRequest {

    @NotNull(message = "Player ID must not be null")
    private Long playerId;

    @NotBlank(message = "Ship name must not be blank")
    @Size(max = 64, message = "Ship name must not exceed 64 characters")
    private String shipName;

    @NotBlank(message = "Session code must not be blank")
    private String sessionCode;

    public Long getPlayerId() {
        return playerId;
    }

    public String getShipName() {
        return shipName;
    }

    public String getSessionCode() {
        return sessionCode;
    }
}