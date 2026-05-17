package at.fhv.blueroute.player.session.client.dto;

public class PlayerSummaryResponse {

    private Long id;
    private String username;
    private Double balance;

    public PlayerSummaryResponse() {
    }

    public PlayerSummaryResponse(Long id, String username, Double balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Double getBalance() {
        return balance;
    }
}