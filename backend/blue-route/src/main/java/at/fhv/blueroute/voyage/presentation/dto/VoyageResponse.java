package at.fhv.blueroute.voyage.presentation.dto;

import java.util.List;

public class VoyageResponse {

    public Long id;
    public Long shipId;

    public String originPort;
    public String destinationPort;

    public String status;

    public int duration;
    public int currentDay;
    public double progress;

    public double reward;

    public List<double[]> route;
}