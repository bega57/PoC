package at.fhv.blueroute.player.presentation.dto;

public class PlayerResponse {

    private Long id;
    private String username;
    private String companyName;
    private Double balance;
    private String currentPort;
    private int points;

    public PlayerResponse(Long id, String username, String companyName, Double balance, String currentPort, int points) {
        this.id = id;
        this.username = username;
        this.companyName = companyName;
        this.balance = balance;
        this.currentPort = currentPort;
        this.points = points;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getCompanyName() { return companyName; }
    public Double getBalance() { return balance; }
    public String getCurrentPort() { return currentPort; }
    public int getPoints() { return points; }
}