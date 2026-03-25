package at.fhv.blueroute.session.presentation.dto;

public class PlayerSummaryResponse {

    private Long id;
    private String username;
    private String companyName;
    private Double balance;

    public PlayerSummaryResponse() {
    }

    public PlayerSummaryResponse(Long id, String username, String companyName, Double balance) {
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

    public String getCompanyName() {
        return companyName;
    }

    public Double getBalance() {
        return balance;
    }
}