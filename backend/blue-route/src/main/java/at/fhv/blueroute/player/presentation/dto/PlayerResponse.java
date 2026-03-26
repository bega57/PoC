package at.fhv.blueroute.player.presentation.dto;

import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import java.util.List;

public class PlayerResponse {

    private Long id;
    private String username;
    private String companyName;
    private double balance;
    private List<ShipResponse> ships;

    public PlayerResponse() {
    }

    public PlayerResponse(Long id, String username, String companyName, double balance, List<ShipResponse> ships) {
        this.id = id;
        this.username = username;
        this.companyName = companyName;
        this.balance = balance;
        this.ships = ships;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ShipResponse> getShips() {
        return ships;
    }
}