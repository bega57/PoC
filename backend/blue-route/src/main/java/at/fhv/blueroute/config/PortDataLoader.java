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

                repo.save(new Port("London", 51.5072, -0.1276, 6.5));
                repo.save(new Port("Hamburg", 54.5511, 9.9937, 6.1));
                repo.save(new Port("Rotterdam", 50.9225, 4.47917, 6.2));

                repo.save(new Port("New York", 40.7128, -74.006, 6.8));
                repo.save(new Port("Los Angeles", 34.0522, -118.2437, 6.7));
                repo.save(new Port("Buenos Aires", -34.6037, -58.3816, 4.8));

                repo.save(new Port("Lagos", 6.5244, 3.3792, 4.2));
                repo.save(new Port("Cape Town", -33.9249, 18.4241, 4.7));

                repo.save(new Port("Dubai", 25.2048, 55.2708, 7.5));
                repo.save(new Port("Singapore", 1.3521, 103.8198, 6.4));
                repo.save(new Port("Tokyo", 35.6895, 139.6917, 7.2));

                System.out.println("CORE PORTS LOADED");
            }
        };
    }
}