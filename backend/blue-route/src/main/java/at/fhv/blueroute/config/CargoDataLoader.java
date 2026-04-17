package at.fhv.blueroute.config;

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

            List<Cargo> existingCargos = cargoRepo.findAll();
            if (!existingCargos.isEmpty()) {

                CalculateFuelConsumptionService fuelService = new CalculateFuelConsumptionService();

                for (Cargo cargo : existingCargos) {

                    if (cargo.getOriginPort() == null || cargo.getDestinationPort() == null) {
                        System.out.println("Cargo " + cargo.getId() + " has missing ports");
                        continue;
                    }

                    System.out.println(
                            cargo.getOriginPort().getName() + " -> " +
                                    cargo.getDestinationPort().getName() +
                                    " | Fuel: " + cargo.getFuelConsumption()
                    );

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
                    }

                    if (cargo.getRequiredCapacity() == 0) {
                        cargo.setRequiredCapacity(100);
                    }

                    if (cargo.getRiskLevel() == null) {
                        cargo.setRiskLevel(RiskLevel.LOW);
                    }

                    int distance = DistanceCalculator.calculate(
                            cargo.getOriginPort(),
                            cargo.getDestinationPort()
                    );

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
            ));

            System.out.println("Cargo loaded safely");
        };
    }

    private Cargo create(Port origin, Port dest, double price, double reward, int capacity, RiskLevel risk) {

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