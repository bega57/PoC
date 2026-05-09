package at.fhv.blueroute.player.domain.model;

import jakarta.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String companyName;
    private Double balance;
    private String currentPort;

    public Player() {
    }

    public Player(String username) {
        this.username = username;
        this.balance = 40000.0;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCurrentPort() {
        return currentPort;
    }

    public void setCurrentPort(String currentPort) {
        this.currentPort = currentPort;
    }
}