package at.fhv.blueroute.ship.player.client.dto;

public class UpdateCompanyNameRequest {

    private String companyName;

    public UpdateCompanyNameRequest() {
    }

    public UpdateCompanyNameRequest(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}