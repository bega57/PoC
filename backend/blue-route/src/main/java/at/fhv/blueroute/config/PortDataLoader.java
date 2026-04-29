package at.fhv.blueroute.config;

import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class PortDataLoader {

    @Bean
    @Order(1)
    public org.springframework.boot.CommandLineRunner initPorts(JpaPortRepository repo) {
        return args -> {

            if (repo.count() == 0) {

                repo.save(new Port("London", 51.5072, -0.1276, 3.2));
                repo.save(new Port("New York", 40.7128, -74.006, 3.5));
                repo.save(new Port("Buenos Aires", -34.6037, -58.3816, 2.1));
                repo.save(new Port("Lima", -12.0464, -77.0428, 2.0));
                repo.save(new Port("Vancouver", 49.2827, -123.1207, 3.2));
                repo.save(new Port("Tokyo", 35.6895, 139.6917, 3.6));
                repo.save(new Port("Shanghai", 31.2304, 121.4737, 3.1));
                repo.save(new Port("Bangkok", 13.7563, 100.5018, 2.0));
                repo.save(new Port("Jakarta", -6.2088, 106.8456, 1.9));
                repo.save(new Port("Istanbul", 41.0082, 28.9784, 2.4));
                repo.save(new Port("Sydney", -33.8688, 151.2093, 2.8));
                repo.save(new Port("Dubai", 25.2048, 55.2708, 3.8));
                repo.save(new Port("Singapore", 1.3521, 103.8198, 3.0));
                repo.save(new Port("Mumbai", 19.076, 72.8777, 2.5));
                repo.save(new Port("Cape Town", -33.9249, 18.4241, 2.2));
                repo.save(new Port("Lagos", 6.5244, 3.3792, 1.7));
                repo.save(new Port("Mombasa", -4.0435, 39.6682, 1.6));
                repo.save(new Port("Rio", -22.9068, -43.1729, 2.0));
                repo.save(new Port("Los Angeles", 34.0522, -118.2437, 3.4));
                repo.save(new Port("Hamburg", 54.5511, 9.9937, 3.0));
                repo.save(new Port("Rotterdam", 50.9225, 4.47917, 3.1));
                repo.save(new Port("Seoul", 37.5665, 126.978, 3.3));
                repo.save(new Port("Honolulu", 21.3069, -157.8583, 2.9));

                System.out.println("ALL PORTS LOADED");
            }
        };
    }
}