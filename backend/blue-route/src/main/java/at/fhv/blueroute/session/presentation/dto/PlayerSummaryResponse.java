package at.fhv.blueroute.session.presentation.dto;

import at.fhv.blueroute.ship.presentation.dto.ShipResponse;

import java.util.List;

public class PlayerSummaryResponse {

    private Long id;
    private String username;
    private String companyName;
    private Double balance;
    private String status;
    private List<ShipResponse> ships;

    public PlayerSummaryResponse() {
    }

    public PlayerSummaryResponse(Long id, String username, String companyName, double balance, String status, List<ShipResponse> ships) {
        this.id = id;
        this.username = username;
        this.companyName = companyName;
        this.balance = balance;
        this.status = status;
        this.ships = ships;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Double getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public List<ShipResponse> getShips() {
        return ships;
    }
}