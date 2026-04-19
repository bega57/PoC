package at.fhv.blueroute.voyage.application.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SeaRouteService {

    private final Map<String, List<double[]>> routes = Map.ofEntries(

            Map.entry("Hamburg-New York", List.of(
                    new double[]{9.99, 53.55},
                    new double[]{5, 54},
                    new double[]{-5, 52},
                    new double[]{-25, 48},
                    new double[]{-50, 45},
                    new double[]{-74.006, 40.7128}
            )),

            Map.entry("London-New York", List.of(
                    new double[]{-0.1276, 51.5072},
                    new double[]{-5, 50},
                    new double[]{-20, 48},
                    new double[]{-40, 45},
                    new double[]{-60, 43},
                    new double[]{-74.006, 40.7128}
            )),

            Map.entry("New York-Los Angeles", List.of(
                    new double[]{-74.006, 40.7128},
                    new double[]{-75, 30},
                    new double[]{-80, 20},
                    new double[]{-85, 15},
                    new double[]{-90, 10},
                    new double[]{-110, 20},
                    new double[]{-118.2437, 34.0522}
            )),

            Map.entry("Los Angeles-Honolulu", List.of(
                    new double[]{-118.2437, 34.0522},
                    new double[]{-140, 30},
                    new double[]{-155, 25},
                    new double[]{-157.8583, 21.3069}
            )),

            Map.entry("Los Angeles-Vancouver", List.of(
                    new double[]{-118.2437, 34.0522},
                    new double[]{-125, 40},
                    new double[]{-125, 48},
                    new double[]{-123.1207, 49.2827}
            )),

            Map.entry("Vancouver-Lima", List.of(
                    new double[]{-123.1207, 49.2827},
                    new double[]{-130, 30},
                    new double[]{-120, 10},
                    new double[]{-100, -10},
                    new double[]{-77.0428, -12.0464}
            )),

            Map.entry("Lima-Buenos Aires", List.of(
                    new double[]{-77.0428, -12.0464},
                    new double[]{-75, -25},
                    new double[]{-65, -30},
                    new double[]{-58.3816, -34.6037}
            )),

            Map.entry("Buenos Aires-Rio", List.of(
                    new double[]{-58.3816, -34.6037},
                    new double[]{-50, -30},
                    new double[]{-45, -25},
                    new double[]{-43.1729, -22.9068}
            )),

            Map.entry("Rio-Lagos", List.of(
                    new double[]{-43.1729, -22.9068},
                    new double[]{-30, -15},
                    new double[]{-10, -5},
                    new double[]{3.3792, 6.5244}
            )),

            Map.entry("Lagos-Cape Town", List.of(
                    new double[]{3.3792, 6.5244},
                    new double[]{8, 0},
                    new double[]{12, -15},
                    new double[]{18.4241, -33.9249}
            )),

            Map.entry("Lagos-London", List.of(
                    new double[]{3.3792, 6.5244},
                    new double[]{0, 10},
                    new double[]{-5, 20},
                    new double[]{-10, 40},
                    new double[]{-0.1276, 51.5072}
            )),

            Map.entry("Cape Town-Mombasa", List.of(
                    new double[]{18.4241, -33.9249},
                    new double[]{25, -20},
                    new double[]{30, -10},
                    new double[]{39.6682, -4.0435}
            )),

            Map.entry("Tokyo-Seoul", List.of(
                    new double[]{139.6917, 35.6895},
                    new double[]{135, 35},
                    new double[]{130, 35},
                    new double[]{126.978, 37.5665}
            )),

            Map.entry("Dubai-Mumbai", List.of(
                    new double[]{55.2708, 25.2048},
                    new double[]{60, 23},
                    new double[]{65, 21},
                    new double[]{72.8777, 19.076}
            )),

            Map.entry("Mumbai-Singapore", List.of(
                    new double[]{72.8777, 19.076},
                    new double[]{80, 15},
                    new double[]{90, 10},
                    new double[]{103.8198, 1.3521}
            )),

            Map.entry("Singapore-Tokyo", List.of(
                    new double[]{103.8198, 1.3521},
                    new double[]{110, 10},
                    new double[]{120, 20},
                    new double[]{130, 30},
                    new double[]{139.6917, 35.6895}
            )),

            Map.entry("Tokyo-Shanghai", List.of(
                    new double[]{139.6917, 35.6895},
                    new double[]{135, 34},
                    new double[]{130, 32},
                    new double[]{121.4737, 31.2304}
            )),

            Map.entry("Shanghai-Bangkok", List.of(
                    new double[]{121.4737, 31.2304},
                    new double[]{115, 25},
                    new double[]{110, 20},
                    new double[]{100.5018, 13.7563}
            )),

            Map.entry("Bangkok-Jakarta", List.of(
                    new double[]{100.5018, 13.7563},
                    new double[]{105, 10},
                    new double[]{110, 5},
                    new double[]{106.8456, -6.2088}
            )),

            Map.entry("Jakarta-Singapore", List.of(
                    new double[]{106.8456, -6.2088},
                    new double[]{104, -2},
                    new double[]{103.8198, 1.3521}
            )),

            Map.entry("New York-Tokyo", List.of(
                    new double[]{-74.006, 40.7128},
                    new double[]{-130, 45},
                    new double[]{-160, 45},
                    new double[]{170, 40},
                    new double[]{139.6917, 35.6895}
            )),

            Map.entry("Tokyo-Sydney", List.of(
                    new double[]{139.6917, 35.6895},
                    new double[]{140, 20},
                    new double[]{145, 5},
                    new double[]{150, -10},
                    new double[]{151.2093, -33.8688}
            )),

            Map.entry("Singapore-Dubai", List.of(
                    new double[]{103.8198, 1.3521},
                    new double[]{90, 5},
                    new double[]{75, 10},
                    new double[]{60, 20},
                    new double[]{55.2708, 25.2048}
            )),

            Map.entry("Dubai-Istanbul", List.of(
                    new double[]{55.2708, 25.2048},
                    new double[]{50, 30},
                    new double[]{40, 35},
                    new double[]{28.9784, 41.0082}
            )),

            Map.entry("Hamburg-Rotterdam", List.of(
                    new double[]{9.99, 53.55},
                    new double[]{7, 53},
                    new double[]{5, 52},
                    new double[]{4.47917, 50.9225}
            )),

            Map.entry("Rotterdam-Istanbul", List.of(
                    new double[]{4.47917, 50.9225},
                    new double[]{10, 45},
                    new double[]{20, 42},
                    new double[]{28.9784, 41.0082}
            ))
    );

    public List<double[]> getRoute(String from, String to) {

        List<double[]> route = routes.get(from + "-" + to);
        if (route != null) return route;

        List<double[]> reverse = routes.get(to + "-" + from);
        if (reverse != null) {
            List<double[]> reversed = new ArrayList<>(reverse);
            Collections.reverse(reversed);
            return reversed;
        }

        return List.of();
    }
}