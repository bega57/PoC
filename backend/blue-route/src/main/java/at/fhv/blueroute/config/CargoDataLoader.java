package at.fhv.blueroute.config;

import at.fhv.blueroute.cargo.application.service.CalculateCargoValuesService;
import at.fhv.blueroute.cargo.application.service.CalculateDeteriorationService;
import at.fhv.blueroute.cargo.application.service.CalculateFuelConsumptionService;
import at.fhv.blueroute.cargo.domain.model.Cargo;
import at.fhv.blueroute.cargo.infrastructure.persistence.JpaCargoRepository;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.infrastructure.persistence.JpaPortRepository;
import at.fhv.blueroute.cargo.domain.model.CargoType;
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
                    create(london, hamburg, "Designer Clothes", CargoType.CLOTHES),
                    create(london, hamburg, "Medical Supplies", CargoType.MEDICINE),
                    create(london, newYork, "Luxury Watches", CargoType.LUXURY_GOODS),

                    create(hamburg, rotterdam, "Fresh Food", CargoType.FOOD),
                    create(hamburg, rotterdam, "Crude Oil Barrels", CargoType.OIL),
                    create(hamburg, newYork, "Industrial Machinery", CargoType.MACHINERY),

                    create(rotterdam, hamburg, "Clothing Containers", CargoType.CLOTHES),
                    create(rotterdam, istanbul, "Electronics Pallets", CargoType.ELECTRONICS),
                    create(rotterdam, istanbul, "Refined Oil", CargoType.OIL),

                    create(istanbul, dubai, "Spice Crates", CargoType.FOOD),
                    create(istanbul, rotterdam, "Machine Parts", CargoType.MACHINERY),
                    create(istanbul, dubai, "Luxury Carpets", CargoType.LUXURY_GOODS),

                    create(dubai, mumbai, "Designer Goods", CargoType.LUXURY_GOODS),
                    create(dubai, singapore, "Oil Drums", CargoType.OIL),
                    create(dubai, istanbul, "Medical Equipment", CargoType.MEDICINE),

                    create(mumbai, dubai, "Cotton Clothes", CargoType.CLOTHES),
                    create(mumbai, singapore, "Smartphones", CargoType.ELECTRONICS),
                    create(mumbai, singapore, "Heavy Machinery", CargoType.MACHINERY),

                    create(singapore, tokyo, "Consumer Electronics", CargoType.ELECTRONICS),
                    create(singapore, dubai, "Frozen Food", CargoType.FOOD),
                    create(singapore, jakarta, "Luxury Jewelry", CargoType.LUXURY_GOODS),

                    create(tokyo, seoul, "Gaming Consoles", CargoType.ELECTRONICS),
                    create(tokyo, shanghai, "Precision Machinery", CargoType.MACHINERY),
                    create(tokyo, sydney, "Medical Robots", CargoType.MEDICINE),

                    create(seoul, tokyo, "Fashion Boxes", CargoType.CLOTHES),
                    create(seoul, tokyo, "Laptop Components", CargoType.ELECTRONICS),
                    create(seoul, tokyo, "Luxury Cosmetics", CargoType.LUXURY_GOODS),

                    create(shanghai, bangkok, "Factory Components", CargoType.MACHINERY),
                    create(shanghai, tokyo, "Electronics Modules", CargoType.ELECTRONICS),
                    create(shanghai, bangkok, "Textile Rolls", CargoType.CLOTHES),

                    create(bangkok, jakarta, "Rice Bags", CargoType.FOOD),
                    create(bangkok, shanghai, "Tourist Goods", CargoType.CLOTHES),
                    create(bangkok, jakarta, "Rare Gemstones", CargoType.LUXURY_GOODS),

                    create(jakarta, singapore, "Tropical Fruit", CargoType.FOOD),
                    create(jakarta, bangkok, "Rubber Machinery", CargoType.MACHINERY),
                    create(jakarta, singapore, "Palm Oil", CargoType.OIL),

                    create(sydney, tokyo, "Frozen Meat", CargoType.FOOD),
                    create(sydney, singapore, "Medical Cargo", CargoType.MEDICINE),
                    create(sydney, tokyo, "Luxury Wine Crates", CargoType.LUXURY_GOODS),

                    create(newYork, losAngeles, "Fashion Retail Cargo", CargoType.CLOTHES),
                    create(newYork, tokyo, "Pharmaceutical Cargo", CargoType.MEDICINE),
                    create(newYork, london, "Luxury Art Pieces", CargoType.LUXURY_GOODS),

                    create(losAngeles, honolulu, "Food Containers", CargoType.FOOD),
                    create(losAngeles, vancouver, "Electronics Shipment", CargoType.ELECTRONICS),
                    create(losAngeles, newYork, "Movie Equipment", CargoType.MACHINERY),

                    create(honolulu, losAngeles, "Fresh Pineapples", CargoType.FOOD),
                    create(honolulu, losAngeles, "Resort Supplies", CargoType.CLOTHES),
                    create(honolulu, losAngeles, "Luxury Pearls", CargoType.LUXURY_GOODS),

                    create(vancouver, lima, "Timber Equipment", CargoType.MACHINERY),
                    create(vancouver, losAngeles, "Winter Clothing", CargoType.CLOTHES),
                    create(vancouver, lima, "Medical Coolers", CargoType.MEDICINE),

                    create(lima, buenosAires, "Coffee Beans", CargoType.FOOD),
                    create(lima, vancouver, "Mining Equipment", CargoType.MACHINERY),
                    create(lima, buenosAires, "Silver Jewelry", CargoType.LUXURY_GOODS),

                    create(buenosAires, rio, "Beef Containers", CargoType.FOOD),
                    create(buenosAires, lima, "Leather Goods", CargoType.CLOTHES),
                    create(buenosAires, rio, "Premium Antiques", CargoType.LUXURY_GOODS),

                    create(rio, lagos, "Fruit Cargo", CargoType.FOOD),
                    create(rio, buenosAires, "Carnival Costumes", CargoType.CLOTHES),
                    create(rio, lagos, "Offshore Oil", CargoType.OIL),

                    create(lagos, capeTown, "Cocoa Bags", CargoType.FOOD),
                    create(lagos, london, "Raw Materials", CargoType.MACHINERY),
                    create(lagos, capeTown, "Fuel Containers", CargoType.OIL),

                    create(capeTown, mombasa, "Fruit Boxes", CargoType.FOOD),
                    create(capeTown, lagos, "Mining Tools", CargoType.MACHINERY),
                    create(capeTown, mombasa, "Luxury Diamonds", CargoType.LUXURY_GOODS),

                    create(mombasa, capeTown, "Tea Crates", CargoType.FOOD),
                    create(mombasa, capeTown, "Medical Aid", CargoType.MEDICINE),
                    create(mombasa, capeTown, "Rare Minerals", CargoType.LUXURY_GOODS)
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

    public static class DistanceCalculator {

        public static int calculate(Port a, Port b) {
            double dx = a.getLatitude() - b.getLatitude();
            double dy = a.getLongitude() - b.getLongitude();
            return (int) Math.sqrt(dx * dx + dy * dy);
        }
    }

}