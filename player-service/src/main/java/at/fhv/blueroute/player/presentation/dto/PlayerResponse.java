package at.fhv.blueroute.player.presentation.dto;

public class PlayerResponse {

    private Long id;
    private String username;
    private String companyName;
    private Double balance;
    private String currentPort;

    public PlayerResponse(Long id, String username, String companyName, Double balance, String currentPort) {
        this.id = id;
        this.username = username;
        this.companyName = companyName;
        this.balance = balance;
        this.currentPort = currentPort;
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

    public String getCurrentPort() {
        return currentPort;
    }
}