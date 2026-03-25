package at.fhv.blueroute.session.presentation.dto;

public class PlayerSummaryResponse {

    private Long id;
    private String username;
    private String companyName;
    private Double balance;
    private String status;

    public PlayerSummaryResponse() {
    }

    public PlayerSummaryResponse(Long id, String username, String companyName, double balance, String status) {
        this.id = id;
        this.username = username;
        this.companyName = companyName;
        this.balance = balance;
        this.status = status;
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
}