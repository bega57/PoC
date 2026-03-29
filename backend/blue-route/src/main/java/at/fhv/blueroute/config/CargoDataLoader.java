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

            Port rio = portRepo.findById("Rio").orElse(null);
            Port newYork = portRepo.findById("New York").orElse(null);
            Port lagos = portRepo.findById("Lagos").orElse(null);
            Port capeTown = portRepo.findById("Cape Town").orElse(null);
            Port london = portRepo.findById("London").orElse(null);
            Port dubai = portRepo.findById("Dubai").orElse(null);
            Port mumbai = portRepo.findById("Mumbai").orElse(null);
            Port singapore = portRepo.findById("Singapore").orElse(null);
            Port tokyo = portRepo.findById("Tokyo").orElse(null);
            Port sydney = portRepo.findById("Sydney").orElse(null);
            Port shanghai = portRepo.findById("Shanghai").orElse(null);
            Port bangkok = portRepo.findById("Bangkok").orElse(null);
            Port jakarta = portRepo.findById("Jakarta").orElse(null);
            Port istanbul = portRepo.findById("Istanbul").orElse(null);
            Port mombasa = portRepo.findById("Mombasa").orElse(null);
            Port losAngeles = portRepo.findById("Los Angeles").orElse(null);
            Port hamburg = portRepo.findById("Hamburg").orElse(null);
            Port rotterdam = portRepo.findById("Rotterdam").orElse(null);
            Port seoul = portRepo.findById("Seoul").orElse(null);
            Port honolulu = portRepo.findById("Honolulu").orElse(null);
            Port buenosAires = portRepo.findById("Buenos Aires").orElse(null);
            Port lima = portRepo.findById("Lima").orElse(null);
            Port vancouver = portRepo.findById("Vancouver").orElse(null);

            cargoRepo.saveAll(
                    java.util.stream.Stream.of(

                                    // AMERICA
                                    create(rio, newYork, 10000),
                                    create(rio, buenosAires, 4000),
                                    create(newYork, losAngeles, 9000),
                                    create(losAngeles, vancouver, 5000),
                                    create(lima, rio, 6000),

                                    // AFRICA
                                    create(lagos, capeTown, 7000),
                                    create(mombasa, lagos, 5000),
                                    create(capeTown, dubai, 8000),

                                    // ASIA
                                    create(dubai, mumbai, 4000),
                                    create(mumbai, singapore, 6000),
                                    create(singapore, tokyo, 9000),
                                    create(shanghai, bangkok, 5000),
                                    create(jakarta, singapore, 3000),
                                    create(seoul, tokyo, 4000),

                                    // EUROPE
                                    create(london, hamburg, 3000),
                                    create(hamburg, rotterdam, 2000),
                                    create(istanbul, dubai, 7000),

                                    // OCEANIA
                                    create(sydney, singapore, 8000),
                                    create(honolulu, losAngeles, 7000)

                            )
                            .filter(c -> c != null)
                            .toList()
            );

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