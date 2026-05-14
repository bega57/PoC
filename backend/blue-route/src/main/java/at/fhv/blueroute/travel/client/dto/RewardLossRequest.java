package at.fhv.blueroute.travel.client.dto;

public class RewardLossRequest {

    private double rewardLossPercent;

    public RewardLossRequest() {
    }

    public RewardLossRequest(double rewardLossPercent) {
        this.rewardLossPercent = rewardLossPercent;
    }

    public double getRewardLossPercent() {
        return rewardLossPercent;
    }
}