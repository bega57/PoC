package at.fhv.blueroute.event.voyage.client.dto;

public class RewardLossRequest {

    private double rewardLossPercent;

    public RewardLossRequest(double rewardLossPercent) {
        this.rewardLossPercent = rewardLossPercent;
    }

    public double getRewardLossPercent() {
        return rewardLossPercent;
    }
}