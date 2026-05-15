package at.fhv.blueroute.voyage.application.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SeaRouteService {

    private final Map<String, List<double[]>> routes = Map.ofEntries(

            Map.entry("London-Hamburg", List.of(
                    p(-0.1276, 51.5072),
                    p(1.0, 51.5),
                    p(3.5, 53.0),
                    p(6.5, 54.0),
                    p(9.9937, 54.5511)
            )),

            Map.entry("Hamburg-Rotterdam", List.of(
                    p(9.9937, 54.5511),
                    p(8.0, 54.2),
                    p(6.0, 53.8),
                    p(4.8, 52.5),
                    p(4.47917, 50.9225)
            )),

            Map.entry("Rotterdam-London", List.of(
                    p(4.47917, 50.9225),
                    p(3.0, 51.2),
                    p(1.0, 51.4),
                    p(-0.1276, 51.5072)
            )),

            Map.entry("London-New York", List.of(
                    p(-0.1276, 51.5072),
                    p(-8.0, 50.0),
                    p(-20.0, 47.0),
                    p(-38.0, 44.0),
                    p(-55.0, 41.0),
                    p(-66.0, 40.0),
                    p(-74.006, 40.7128)
            )),

            Map.entry("Rotterdam-New York", List.of(
                    p(4.47917, 50.9225),
                    p(-2.0, 51.0),
                    p(-15.0, 48.0),
                    p(-32.0, 45.0),
                    p(-50.0, 42.0),
                    p(-65.0, 40.5),
                    p(-74.006, 40.7128)
            )),

            Map.entry("New York-Los Angeles", List.of(
                    p(-74.006, 40.7128),
                    p(-77.0, 33.0),
                    p(-80.0, 25.0),
                    p(-81.0, 18.0),
                    p(-79.5, 9.0),
                    p(-90.0, 10.0),
                    p(-105.0, 18.0),
                    p(-118.2437, 34.0522)
            )),

            Map.entry("Los Angeles-Buenos Aires", List.of(
                    p(-118.2437, 34.0522),
                    p(-125.0, 20.0),
                    p(-120.0, 5.0),
                    p(-105.0, -10.0),
                    p(-92.0, -25.0),
                    p(-82.0, -40.0),
                    p(-74.0, -55.0),
                    p(-62.0, -45.0),
                    p(-58.3816, -34.6037)
            )),

            Map.entry("Buenos Aires-New York", List.of(
                    p(-58.3816, -34.6037),
                    p(-50.0, -38.0),
                    p(-42.0, -25.0),
                    p(-35.0, -8.0),
                    p(-40.0, 8.0),
                    p(-48.0, 20.0),
                    p(-58.0, 32.0),
                    p(-68.0, 38.0),
                    p(-74.006, 40.7128)
            )),

            Map.entry("London-Lagos", List.of(
                    p(-0.1276, 51.5072),
                    p(-6.0, 48.0),
                    p(-10.0, 38.0),
                    p(-14.0, 25.0),
                    p(-12.0, 12.0),
                    p(-5.0, 5.0),
                    p(3.3792, 6.5244)
            )),

            Map.entry("Rotterdam-Cape Town", List.of(
                    p(4.47917, 50.9225),
                    p(-4.0, 47.0),
                    p(-10.0, 35.0),
                    p(-15.0, 20.0),
                    p(-10.0, 5.0),
                    p(0.0, -12.0),
                    p(10.0, -24.0),
                    p(18.4241, -33.9249)
            )),

            Map.entry("Lagos-Cape Town", List.of(
                    p(3.3792, 6.5244),
                    p(5.0, -2.0),
                    p(8.0, -12.0),
                    p(12.0, -22.0),
                    p(15.0, -30.0),
                    p(18.4241, -33.9249)
            )),

            Map.entry("Cape Town-Dubai", List.of(
                    p(18.4241, -33.9249),
                    p(35.0, -35.0),
                    p(50.0, -28.0),
                    p(60.0, -15.0),
                    p(62.0, 0.0),
                    p(58.0, 15.0),
                    p(55.2708, 25.2048)
            )),

            Map.entry("Lagos-Dubai", List.of(
                    p(3.3792, 6.5244),
                    p(5.0, -10.0),
                    p(12.0, -22.0),
                    p(22.0, -35.0),
                    p(40.0, -30.0),
                    p(55.0, -10.0),
                    p(60.0, 10.0),
                    p(55.2708, 25.2048)
            )),

            Map.entry("Dubai-Singapore", List.of(
                    p(55.2708, 25.2048),
                    p(62.0, 18.0),
                    p(72.0, 10.0),
                    p(82.0, 6.0),
                    p(95.0, 4.0),
                    p(103.8198, 1.3521)
            )),

            Map.entry("Singapore-Tokyo", List.of(
                    p(103.8198, 1.3521),
                    p(108.0, 8.0),
                    p(115.0, 16.0),
                    p(125.0, 25.0),
                    p(132.0, 31.0),

                    p(139.6917, 35.6895)
            )),

            Map.entry("Los Angeles-Tokyo", List.of(
                    p(-118.2437, 34.0522),
                    p(-135.0, 36.0),
                    p(-155.0, 39.0),
                    p(175.0, 40.0),
                    p(155.0, 38.0),
                    p(145.0, 36.0),
                    p(139.6917, 35.6895)
            )),

            Map.entry("New York-Dubai", List.of(
                    p(-74.006, 40.7128),
                    p(-55.0, 39.0),
                    p(-35.0, 37.0),
                    p(-15.0, 36.0),
                    p(5.0, 36.0),
                    p(20.0, 35.0),
                    p(32.0, 30.0),
                    p(40.0, 20.0),
                    p(50.0, 18.0),
                    p(55.2708, 25.2048)
            )),

            Map.entry("London-Singapore", List.of(
                    p(-0.1276, 51.5072),
                    p(-6.0, 48.0),
                    p(-8.0, 38.0),
                    p(10.0, 36.0),
                    p(24.0, 34.0),
                    p(32.0, 30.0),
                    p(45.0, 18.0),
                    p(60.0, 10.0),
                    p(80.0, 5.0),
                    p(95.0, 4.0),
                    p(103.8198, 1.3521)
            )),

            Map.entry("Buenos Aires-Tokyo", List.of(
                    p(-58.3816, -34.6037),
                    p(-68.0, -52.0),
                    p(-100.0, -45.0),
                    p(-140.0, -35.0),
                    p(175.0, -25.0),
                    p(160.0, 0.0),
                    p(150.0, 20.0),
                    p(145.0, 30.0),
                    p(139.6917, 35.6895)
            ))
    );

    private static List<double[]> smooth(List<double[]> route) {

        List<double[]> result = new ArrayList<>();

        for (int i = 0; i < route.size() - 1; i++) {

            double[] start = route.get(i);
            double[] end = route.get(i + 1);

            result.add(start);

            int steps = 30;

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

        if (route != null) {
            return smooth(route);
        }

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