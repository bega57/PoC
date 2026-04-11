package at.fhv.blueroute.config;

import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CargoDataLoader {

    @Bean
    public org.springframework.boot.CommandLineRunner initCargo(
            JpaCargoRepository cargoRepo,
            JpaPortRepository portRepo
    ) {
        return args -> {

            List<Cargo> existingCargos = cargoRepo.findAll();
            if (!existingCargos.isEmpty()) {

                for (Cargo cargo : existingCargos) {

                    if (cargo.getRequiredTicks() == 0) {
                        int distance = DistanceCalculator.calculate(
                                cargo.getOriginPort(),
                                cargo.getDestinationPort()
                        );
                        int ticks = Math.max(1, distance / 10);
                        cargo.setRequiredTicks(ticks);
                    }

                    if (cargo.getReward() == 0) {
                        cargo.setReward(cargo.getPrice() * 1.2);
                        cargo.setRequiredCapacity(100);
                        cargo.setRiskLevel(RiskLevel.LOW);
                    }
                }

                cargoRepo.saveAll(existingCargos);
                System.out.println("Existing cargos fixed");
                return;
            }

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

            cargoRepo.saveAll
                    (List.of(

                    create(london, hamburg, 3000, 4200, 30, RiskLevel.LOW),
                    create(hamburg, rotterdam, 2000, 2800, 25, RiskLevel.LOW),
                    create(rotterdam, istanbul, 7000, 9800, 80, RiskLevel.MEDIUM),

                    create(lagos, capeTown, 7000, 11000, 90, RiskLevel.MEDIUM),
                    create(capeTown, mombasa, 5000, 7600, 70, RiskLevel.MEDIUM),

                    create(dubai, mumbai, 4000, 5600, 40, RiskLevel.LOW),
                    create(mumbai, singapore, 6000, 9000, 80, RiskLevel.MEDIUM),
                    create(singapore, tokyo, 9000, 14000, 120, RiskLevel.HIGH),
                    create(tokyo, seoul, 4000, 6000, 50, RiskLevel.LOW),

                    create(newYork, losAngeles, 9000, 15000, 150, RiskLevel.HIGH),
                    create(losAngeles, vancouver, 5000, 7600, 70, RiskLevel.MEDIUM),
                    create(vancouver, lima, 6000, 9000, 80, RiskLevel.MEDIUM),
                    create(lima, buenosAires, 4000, 6000, 50, RiskLevel.LOW),
                    create(buenosAires, rio, 4000, 6000, 50, RiskLevel.LOW),

                    create(rio, lagos, 7000, 11000, 100, RiskLevel.MEDIUM),
                    create(lagos, london, 8000, 13000, 120, RiskLevel.HIGH),
                    create(london, newYork, 9000, 15000, 140, RiskLevel.HIGH),
                    create(newYork, tokyo, 12000, 20000, 180, RiskLevel.HIGH),
                    create(tokyo, sydney, 11000, 18000, 160, RiskLevel.HIGH),
                    create(sydney, singapore, 8000, 12000, 100, RiskLevel.MEDIUM),
                    create(singapore, dubai, 7000, 10500, 90, RiskLevel.MEDIUM),
                    create(dubai, istanbul, 6000, 9000, 80, RiskLevel.MEDIUM),

                    create(honolulu, losAngeles, 7000, 11000, 100, RiskLevel.MEDIUM),
                    create(jakarta, singapore, 3000, 4200, 35, RiskLevel.LOW),
                    create(shanghai, bangkok, 5000, 7600, 70, RiskLevel.MEDIUM)

            ).stream().filter(c -> c != null).toList()
                    );

            System.out.println("Cargo loaded safely");
        };
    }

    private Cargo create(Port origin, Port dest, double price, double reward, int capacity, RiskLevel risk) {
        if (origin == null || dest == null) return null;

        Cargo c = new Cargo();
        c.setOriginPort(origin);
        c.setDestinationPort(dest);
        c.setPrice(price);
        c.setReward(reward);
        c.setRequiredCapacity(capacity);
        c.setRiskLevel(risk);

        int distance = DistanceCalculator.calculate(origin, dest);
        int requiredTicks = Math.max(1, distance / 10);
        c.setRequiredTicks(requiredTicks);

        return c;
    }

    public class DistanceCalculator {

        public static int calculate(Port a, Port b) {
            double dx = a.getLatitude() - b.getLatitude();
            double dy = a.getLongitude() - b.getLongitude();
            return (int) Math.sqrt(dx * dx + dy * dy);
        }
    }

}