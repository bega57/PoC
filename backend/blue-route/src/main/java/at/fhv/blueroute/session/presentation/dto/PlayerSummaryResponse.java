package at.fhv.blueroute.session.presentation.dto;

public class PlayerSummaryResponse {

    private Long id;
    private String username;

    public PlayerSummaryResponse() {
    }

    public PlayerSummaryResponse(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}