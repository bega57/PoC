package at.fhv.blueroute.config;

import at.fhv.blueroute.cargo.application.service.CalculateCargoValuesService;
import at.fhv.blueroute.cargo.application.service.CalculateDeteriorationService;
import at.fhv.blueroute.cargo.application.service.CalculateFuelConsumptionService;
import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.RiskLevel;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class CargoDataLoader {

    private Port getPort(JpaPortRepository portRepo, String name) {
        return portRepo.findByName(name)
                .orElseThrow(() -> new RuntimeException("Port not found: " + name));
    }

    @Bean
    @Order(2)
    public org.springframework.boot.CommandLineRunner initCargo(
            JpaCargoRepository cargoRepo,
            JpaPortRepository portRepo
    ) {
        return args -> {

            CalculateCargoValuesService valueService =
                    new CalculateCargoValuesService(new CalculateDeteriorationService());
            CalculateFuelConsumptionService fuelService = new CalculateFuelConsumptionService();

            List<Cargo> existingCargos = cargoRepo.findAll();
            if (!existingCargos.isEmpty()) {

                for (Cargo cargo : existingCargos) {

                    if (cargo.getOriginPort() == null || cargo.getDestinationPort() == null) {
                        System.out.println("Cargo " + cargo.getId() + " has missing ports");
                        continue;
                    }

                    int distance = DistanceCalculator.calculate(
                            cargo.getOriginPort(),
                            cargo.getDestinationPort()
                    );


                    System.out.println(
                            cargo.getOriginPort().getName() + " -> " +
                                    cargo.getDestinationPort().getName() +
                                    " | Price: " + cargo.getPrice() +
                                    " | Reward: " + cargo.getReward() +
                                    " | Risk: " + cargo.getRiskLevel()
                    );

                    if (cargo.getRequiredTicks() == 0) {
                        int ticks = Math.max(1, distance / 10);
                        cargo.setRequiredTicks(ticks);
                    }

                    valueService.apply(cargo, distance);

                    double fuel = fuelService.calculate(cargo, distance);
                    cargo.setFuelConsumption(fuel);
                }

                cargoRepo.saveAll(existingCargos);
                System.out.println("Existing cargos fixed");
                return;
            }

            Port rio = getPort(portRepo, "Rio");
            Port newYork = getPort(portRepo, "New York");
            Port lagos = getPort(portRepo, "Lagos");
            Port capeTown = getPort(portRepo, "Cape Town");
            Port london = getPort(portRepo, "London");
            Port dubai = getPort(portRepo, "Dubai");
            Port mumbai = getPort(portRepo, "Mumbai");
            Port singapore = getPort(portRepo, "Singapore");
            Port tokyo = getPort(portRepo, "Tokyo");
            Port sydney = getPort(portRepo, "Sydney");
            Port shanghai = getPort(portRepo, "Shanghai");
            Port bangkok = getPort(portRepo, "Bangkok");
            Port jakarta = getPort(portRepo, "Jakarta");
            Port istanbul = getPort(portRepo, "Istanbul");
            Port mombasa = getPort(portRepo, "Mombasa");
            Port losAngeles = getPort(portRepo, "Los Angeles");
            Port hamburg = getPort(portRepo, "Hamburg");
            Port rotterdam = getPort(portRepo, "Rotterdam");
            Port seoul = getPort(portRepo, "Seoul");
            Port honolulu = getPort(portRepo, "Honolulu");
            Port buenosAires = getPort(portRepo, "Buenos Aires");
            Port lima = getPort(portRepo, "Lima");
            Port vancouver = getPort(portRepo, "Vancouver");

            cargoRepo.saveAll(List.of(
                    create(london, hamburg),
                    create(hamburg, rotterdam),
                    create(rotterdam, istanbul),

                    create(lagos, capeTown),
                    create(capeTown, mombasa),

                    create(dubai, mumbai),
                    create(mumbai, singapore),
                    create(singapore, tokyo),
                    create(tokyo, seoul),

                    create(newYork, losAngeles),
                    create(losAngeles, vancouver),
                    create(vancouver, lima),
                    create(lima, buenosAires),
                    create(buenosAires, rio),

                    create(rio, lagos),
                    create(lagos, london),
                    create(london, newYork),
                    create(newYork, tokyo),
                    create(tokyo, sydney),
                    create(sydney, singapore),
                    create(singapore, dubai),
                    create(dubai, istanbul),

                    create(honolulu, losAngeles),
                    create(jakarta, singapore),
                    create(shanghai, bangkok)
            ));
            System.out.println("Cargo loaded safely");
        };
    }

    private Cargo create(Port origin, Port dest) {

        Cargo c = new Cargo();
        c.setOriginPort(origin);
        c.setDestinationPort(dest);

        int distance = DistanceCalculator.calculate(origin, dest);

        int requiredTicks = Math.max(1, distance / 10);
        c.setRequiredTicks(requiredTicks);

        CalculateCargoValuesService valueService =
                new CalculateCargoValuesService(new CalculateDeteriorationService());
        valueService.apply(c, distance);

        CalculateFuelConsumptionService fuelService = new CalculateFuelConsumptionService();
        double fuel = fuelService.calculate(c, distance);
        c.setFuelConsumption(fuel);

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