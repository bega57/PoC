package at.fhv.blueroute.ship.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BuyShipRequest {

    @NotNull(message = "Player ID must not be null")
    private Long playerId;

    @Size(max = 64, message = "Company name must not exceed 64 characters")
    private String companyName;

    @NotBlank(message = "Ship name must not be blank")
    @Size(max = 64, message = "Ship name must not exceed 64 characters")
    private String shipName;

    @NotBlank(message = "Ship type must not be blank")
    private String shipType;

    @NotBlank(message = "Session code must not be blank")
    private String sessionCode;

    public BuyShipRequest() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getShipName() {
        return shipName;
    }

    public String getShipType() {
        return shipType;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

}