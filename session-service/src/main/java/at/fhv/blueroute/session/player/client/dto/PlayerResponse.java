package at.fhv.blueroute.session.player.client.dto;

public class PlayerResponse {
    private Long id;
    private String username;
    private String companyName;
    private Double balance;
    private String currentPort;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getCompanyName() { return companyName; }
    public Double getBalance() { return balance; }
    public String getCurrentPort() { return currentPort; }
}