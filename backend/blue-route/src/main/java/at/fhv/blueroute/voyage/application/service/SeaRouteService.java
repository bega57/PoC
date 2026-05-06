package at.fhv.blueroute.voyage.application.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SeaRouteService {

    private final Map<String, List<double[]>> routes = Map.ofEntries(

            // EUROPE
            Map.entry("London-Hamburg", List.of(
                    p(-0.1276, 51.5072),
                    p(1.5, 51.2),
                    p(3.5, 52.5),
                    p(6.5, 54.0),
                    p(9.9937, 54.5511)
            )),

            Map.entry("Hamburg-Rotterdam", List.of(
                    p(9.9937, 54.5511),
                    p(8.5, 54.3),
                    p(6.0, 53.8),
                    p(4.0, 52.2),
                    p(4.47917, 50.9225)
            )),

            Map.entry("Rotterdam-London", List.of(
                    p(4.47917, 50.9225),
                    p(3.0, 51.4),
                    p(1.0, 51.2),
                    p(-0.1276, 51.5072)
            )),

            // EUROPE -> AMERICA
            Map.entry("London-New York", List.of(
                    p(-0.1276, 51.5072),
                    p(-5.0, 50.0),
                    p(-15.0, 48.5),
                    p(-35.0, 45.5),
                    p(-55.0, 42.5),
                    p(-74.006, 40.7128)
            )),

            Map.entry("Rotterdam-New York", List.of(
                    p(4.47917, 50.9225),
                    p(1.0, 51.5),
                    p(-8.0, 50.0),
                    p(-25.0, 47.0),
                    p(-50.0, 43.5),
                    p(-74.006, 40.7128)
            )),

            // AMERICA
            Map.entry("New York-Los Angeles", List.of(
                    p(-74.006, 40.7128),
                    p(-76.0, 35.0),
                    p(-80.0, 26.0),
                    p(-82.0, 20.0),
                    p(-81.0, 9.0),
                    p(-79.5, 8.8),
                    p(-90.0, 10.0),
                    p(-105.0, 18.0),
                    p(-118.2437, 34.0522)
            )),

            Map.entry("Los Angeles-Buenos Aires", List.of(
                    p(-118.2437, 34.0522),
                    p(-110.0, 20.0),
                    p(-95.0, 8.0),
                    p(-82.0, -5.0),
                    p(-76.0, -20.0),
                    p(-74.0, -45.0),
                    p(-67.0, -55.0),
                    p(-58.3816, -34.6037)
            )),

            Map.entry("Buenos Aires-New York", List.of(
                    p(-58.3816, -34.6037),
                    p(-50.0, -20.0),
                    p(-42.0, -5.0),
                    p(-45.0, 10.0),
                    p(-55.0, 25.0),
                    p(-70.0, 35.0),
                    p(-74.006, 40.7128)
            )),

            // EUROPE -> AFRICA
            Map.entry("London-Lagos", List.of(
                    p(-0.1276, 51.5072),
                    p(-5.0, 50.0),
                    p(-10.0, 43.0),
                    p(-9.0, 36.0),
                    p(-15.0, 20.0),
                    p(-10.0, 5.0),
                    p(3.3792, 6.5244)
            )),

            Map.entry("Rotterdam-Cape Town", List.of(
                    p(4.47917, 50.9225),
                    p(-5.0, 48.0),
                    p(-10.0, 36.0),
                    p(-15.0, 20.0),
                    p(-5.0, 0.0),
                    p(5.0, -20.0),
                    p(18.4241, -33.9249)
            )),

            // AFRICA
            Map.entry("Lagos-Cape Town", List.of(
                    p(3.3792, 6.5244),
                    p(4.0, 0.0),
                    p(8.0, -12.0),
                    p(12.0, -24.0),
                    p(18.4241, -33.9249)
            )),

            // AFRICA -> ASIA
            Map.entry("Cape Town-Dubai", List.of(
                    p(18.4241, -33.9249),
                    p(35.0, -35.0),
                    p(50.0, -25.0),
                    p(58.0, -10.0),
                    p(60.0, 10.0),
                    p(55.2708, 25.2048)
            )),

            Map.entry("Lagos-Dubai", List.of(
                    p(3.3792, 6.5244),
                    p(5.0, -5.0),
                    p(12.0, -22.0),
                    p(20.0, -35.0),
                    p(40.0, -30.0),
                    p(55.0, -10.0),
                    p(60.0, 10.0),
                    p(55.2708, 25.2048)
            )),

            // ASIA
            Map.entry("Dubai-Singapore", List.of(
                    p(55.2708, 25.2048),
                    p(60.0, 18.0),
                    p(70.0, 10.0),
                    p(80.0, 5.0),
                    p(95.0, 4.0),
                    p(103.8198, 1.3521)
            )),

            Map.entry("Singapore-Tokyo", List.of(
                    p(103.8198, 1.3521),
                    p(108.0, 8.0),
                    p(116.0, 18.0),
                    p(125.0, 26.0),
                    p(135.0, 32.0),
                    p(139.6917, 35.6895)
            )),

            // GLOBAL LONG ROUTES
            Map.entry("Los Angeles-Tokyo", List.of(
                    p(-118.2437, 34.0522),
                    p(-135.0, 35.0),
                    p(-155.0, 38.0),
                    p(175.0, 39.0),
                    p(155.0, 38.0),
                    p(139.6917, 35.6895)
            )),

            Map.entry("New York-Dubai", List.of(
                    p(-74.006, 40.7128),
                    p(-50.0, 38.0),
                    p(-25.0, 36.0),
                    p(-6.0, 36.0),
                    p(12.0, 36.0),
                    p(30.0, 31.0),
                    p(34.0, 28.0),
                    p(43.0, 13.0),
                    p(55.2708, 25.2048)
            )),

            Map.entry("London-Singapore", List.of(
                    p(-0.1276, 51.5072),
                    p(-5.0, 50.0),
                    p(-6.0, 36.0),
                    p(12.0, 36.0),
                    p(30.0, 31.0),
                    p(34.0, 28.0),
                    p(43.0, 13.0),
                    p(60.0, 10.0),
                    p(80.0, 5.0),
                    p(95.0, 4.0),
                    p(103.8198, 1.3521)
            )),

            Map.entry("Buenos Aires-Tokyo", List.of(
                    p(-58.3816, -34.6037),
                    p(-67.0, -55.0),
                    p(-100.0, -45.0),
                    p(-140.0, -35.0),
                    p(175.0, -25.0),
                    p(155.0, 10.0),
                    p(145.0, 28.0),
                    p(139.6917, 35.6895)
            ))
    );

    private static List<double[]> smooth(List<double[]> route) {
        List<double[]> result = new ArrayList<>();

        for (int i = 0; i < route.size() - 1; i++) {
            double[] start = route.get(i);
            double[] end = route.get(i + 1);

            result.add(start);

            int steps = 15;

            for (int j = 1; j < steps; j++) {
                double t = (double) j / steps;

                double lon = start[0] + (end[0] - start[0]) * t;
                double lat = start[1] + (end[1] - start[1]) * t;

                result.add(new double[]{lon, lat});
            }
        }

        result.add(route.get(route.size() - 1));
        return result;
    }

    public List<double[]> getRoute(String from, String to) {

        List<double[]> route = routes.get(from + "-" + to);
        if (route != null) return smooth(route);

        List<double[]> reverse = routes.get(to + "-" + from);
        if (reverse != null) {
            List<double[]> reversed = new ArrayList<>(reverse);
            Collections.reverse(reversed);
            return smooth(reversed);
        }

        return List.of();
    }

    private static double[] p(double lon, double lat) {
        return new double[]{lon, lat};
    }
}