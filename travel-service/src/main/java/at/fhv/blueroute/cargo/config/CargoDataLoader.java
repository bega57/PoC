package at.fhv.blueroute.cargo.config;

import at.fhv.blueroute.cargo.application.service.CalculateCargoValuesService;
import at.fhv.blueroute.cargo.application.service.CalculateDeteriorationService;
import at.fhv.blueroute.cargo.application.service.CalculateFuelConsumptionService;
import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.domain.model.CargoType;
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

                    int ticks = Math.max(2, (int) Math.ceil(distance / 1400.0));
                    cargo.setRequiredTicks(ticks);

                    if (cargo.getName() == null || cargo.getName().isBlank()) {
                        cargo.setName("Unknown Cargo");
                    }

                    if (cargo.getType() == null) {
                        cargo.setType(CargoType.CLOTHES);
                    }

                    valueService.apply(cargo, distance);

                    double fuel = fuelService.calculate(cargo, distance);
                    cargo.setFuelConsumption(fuel);
                }

                cargoRepo.saveAll(existingCargos);
                System.out.println("Existing cargos fixed");
                return;
            }

            Port london = getPort(portRepo, "London");
            Port hamburg = getPort(portRepo, "Hamburg");
            Port rotterdam = getPort(portRepo, "Rotterdam");

            Port newYork = getPort(portRepo, "New York");
            Port losAngeles = getPort(portRepo, "Los Angeles");
            Port buenosAires = getPort(portRepo, "Buenos Aires");

            Port lagos = getPort(portRepo, "Lagos");
            Port capeTown = getPort(portRepo, "Cape Town");

            Port dubai = getPort(portRepo, "Dubai");
            Port singapore = getPort(portRepo, "Singapore");
            Port tokyo = getPort(portRepo, "Tokyo");

            cargoRepo.saveAll(List.of(

                    // EUROPE
                    create(london, hamburg, "Designer Clothes", CargoType.CLOTHES),
                    create(hamburg, rotterdam, "Industrial Goods", CargoType.MACHINERY),
                    create(rotterdam, london, "Electronics", CargoType.ELECTRONICS),

                    // EUROPE -> AMERICA
                    create(london, newYork, "Luxury Goods", CargoType.LUXURY_GOODS),
                    create(rotterdam, newYork, "Machinery", CargoType.MACHINERY),

                    // AMERICA
                    create(newYork, losAngeles, "Retail Cargo", CargoType.CLOTHES),
                    create(losAngeles, buenosAires, "Food Containers", CargoType.FOOD),
                    create(buenosAires, newYork, "Beef Export", CargoType.FOOD),

                    // EUROPE -> AFRICA
                    create(london, lagos, "Raw Materials", CargoType.MACHINERY),
                    create(rotterdam, capeTown, "Oil Cargo", CargoType.OIL),

                    // AFRICA
                    create(lagos, capeTown, "Cocoa Trade", CargoType.FOOD),
                    create(capeTown, lagos, "Mining Equipment", CargoType.MACHINERY),

                    // AFRICA -> ASIA
                    create(capeTown, dubai, "Luxury Minerals", CargoType.LUXURY_GOODS),
                    create(lagos, dubai, "Oil Shipment", CargoType.OIL),

                    // ASIA
                    create(dubai, singapore, "Fuel Cargo", CargoType.OIL),
                    create(singapore, tokyo, "Consumer Electronics", CargoType.ELECTRONICS),
                    create(tokyo, singapore, "Tech Parts", CargoType.ELECTRONICS),

                    // GLOBAL LONG ROUTES
                    create(losAngeles, tokyo, "High Tech Cargo", CargoType.ELECTRONICS),
                    create(newYork, dubai, "Medical Supplies", CargoType.MEDICINE),
                    create(london, singapore, "Luxury Trade", CargoType.LUXURY_GOODS),
                    create(buenosAires, tokyo, "Premium Goods", CargoType.LUXURY_GOODS)

            ));
            System.out.println("Cargo loaded safely");
        };
    }

    private Cargo create(Port origin, Port dest, String name, CargoType type) {

        Cargo c = new Cargo();

        c.setName(name);
        c.setType(type);

        c.setOriginPort(origin);
        c.setDestinationPort(dest);

        int distance = DistanceCalculator.calculate(origin, dest);

        int requiredTicks = Math.max(2, (int) Math.ceil(distance / 1400.0));
        c.setRequiredTicks(requiredTicks);

        CalculateCargoValuesService valueService =
                new CalculateCargoValuesService(new CalculateDeteriorationService());
        valueService.apply(c, distance);

        CalculateFuelConsumptionService fuelService = new CalculateFuelConsumptionService();
        double fuel = fuelService.calculate(c, distance);
        c.setFuelConsumption(fuel);

        return c;
    }

    public static class DistanceCalculator {

        public static int calculate(Port a, Port b) {
            double dx = a.getLatitude() - b.getLatitude();
            double dy = a.getLongitude() - b.getLongitude();
            return (int) (Math.sqrt(dx * dx + dy * dy) * 100);
        }
    }

}