package at.fhv.blueroute.config;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CargoDataLoader {

    @Bean
    public org.springframework.boot.CommandLineRunner initCargo(
            JpaCargoRepository cargoRepo,
            JpaPortRepository portRepo
    ) {
        return args -> {

            if (cargoRepo.count() > 0) return;

            Port rio = portRepo.findByName("Rio").orElse(null);
            Port newYork = portRepo.findByName("New York").orElse(null);
            Port lagos = portRepo.findByName("Lagos").orElse(null);
            Port capeTown = portRepo.findByName("Cape Town").orElse(null);
            Port london = portRepo.findByName("London").orElse(null);
            Port dubai = portRepo.findByName("Dubai").orElse(null);
            Port mumbai = portRepo.findByName("Mumbai").orElse(null);
            Port singapore = portRepo.findByName("Singapore").orElse(null);
            Port tokyo = portRepo.findByName("Tokyo").orElse(null);
            Port sydney = portRepo.findByName("Sydney").orElse(null);
            Port shanghai = portRepo.findByName("Shanghai").orElse(null);
            Port bangkok = portRepo.findByName("Bangkok").orElse(null);
            Port jakarta = portRepo.findByName("Jakarta").orElse(null);
            Port istanbul = portRepo.findByName("Istanbul").orElse(null);
            Port mombasa = portRepo.findByName("Mombasa").orElse(null);
            Port losAngeles = portRepo.findByName("Los Angeles").orElse(null);
            Port hamburg = portRepo.findByName("Hamburg").orElse(null);
            Port rotterdam = portRepo.findByName("Rotterdam").orElse(null);
            Port seoul = portRepo.findByName("Seoul").orElse(null);
            Port honolulu = portRepo.findByName("Honolulu").orElse(null);
            Port buenosAires = portRepo.findByName("Buenos Aires").orElse(null);
            Port lima = portRepo.findByName("Lima").orElse(null);
            Port vancouver = portRepo.findByName("Vancouver").orElse(null);

            cargoRepo.saveAll(List.of(

                    // EUROPE
                    create(london, hamburg, 3000),
                    create(hamburg, rotterdam, 2000),
                    create(rotterdam, istanbul, 7000),

                    // AFRICA
                    create(lagos, capeTown, 7000),
                    create(capeTown, mombasa, 5000),

                    // ASIA
                    create(dubai, mumbai, 4000),
                    create(mumbai, singapore, 6000),
                    create(singapore, tokyo, 9000),
                    create(tokyo, seoul, 4000),

                    // AMERICA
                    create(newYork, losAngeles, 9000),
                    create(losAngeles, vancouver, 5000),
                    create(vancouver, lima, 6000),
                    create(lima, buenosAires, 4000),
                    create(buenosAires, rio, 4000),

                    // GLOBAL LINKS 🔥 (wichtig!)
                    create(rio, lagos, 7000),
                    create(lagos, london, 8000),
                    create(london, newYork, 9000),
                    create(newYork, tokyo, 12000),
                    create(tokyo, sydney, 11000),
                    create(sydney, singapore, 8000),
                    create(singapore, dubai, 7000),
                    create(dubai, istanbul, 6000),

                    // BONUS ROUTES
                    create(honolulu, losAngeles, 7000),
                    create(jakarta, singapore, 3000),
                    create(shanghai, bangkok, 5000)

            ));

            System.out.println("Cargo loaded safely");
        };
    }

    private Cargo create(Port origin, Port dest, double price) {
        if (origin == null || dest == null) return null;

        Cargo c = new Cargo();
        c.setOriginPort(origin);
        c.setDestinationPort(dest);
        c.setPrice(price);
        return c;
    }
}