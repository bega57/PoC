package at.fhv.blueroute.voyage.domain.model;

import java.util.List;

public class SeaRoute {

    private String from;
    private String to;
    private List<double[]> waypoints;

    public SeaRoute(String from, String to, List<double[]> waypoints) {
        this.from = from;
        this.to = to;
        this.waypoints = waypoints;
    }

    public List<double[]> getWaypoints() {
        return waypoints;
    }

    public boolean matches(String a, String b) {
        return from.equals(a) && to.equals(b);
    }
}
