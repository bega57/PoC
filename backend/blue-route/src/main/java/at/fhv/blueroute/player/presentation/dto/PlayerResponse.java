package at.fhv.blueroute.player.presentation.dto;

public class PlayerResponse {

    private Long id;
    private String username;
    private String companyName;
    private double balance;

    public PlayerResponse() {
    }

    public PlayerResponse(Long id, String username, String companyName, double balance) {
        this.id = id;
        this.username = username;
        this.companyName = companyName;
        this.balance = balance;
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
}