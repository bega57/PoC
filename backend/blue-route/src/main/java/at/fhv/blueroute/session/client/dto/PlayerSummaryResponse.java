package at.fhv.blueroute.session.client.dto;


public class PlayerSummaryResponse {

    private Long id;
    private String username;
    private String companyName;
    private Double balance;
    private String status;
    private String currentPort;

    public PlayerSummaryResponse() {
    }

    public PlayerSummaryResponse(Long id, String username,
                                 String companyName,
                                 double balance,
                                 String status,
                                 String currentPort) {
        this.id = id;
        this.username = username;
        this.companyName = companyName;
        this.balance = balance;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public String getCurrentPort() {
        return currentPort;
    }
}