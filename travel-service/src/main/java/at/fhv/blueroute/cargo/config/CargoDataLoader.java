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

                    // ── LONDON (origin) ──────────────────────────────────────────────
                    create(london, hamburg,    "Designer Clothes",      CargoType.CLOTHES),       // small, cheap, LOW risk
                    create(london, newYork,    "Luxury Goods",          CargoType.LUXURY_GOODS),  // small, expensive, HIGH risk
                    create(london, lagos,      "Raw Materials",         CargoType.MACHINERY),     // large, MEDIUM risk
                    createSmall(london, rotterdam, "Diplomatic Parcels",CargoType.MEDICINE, 12),  // tiny, fits cutter
                    create(london, dubai,      "Whisky & Spirits",      CargoType.LUXURY_GOODS),  // high reward long route
                    create(london, singapore,  "Luxury Trade",          CargoType.LUXURY_GOODS),

                    // ── HAMBURG (origin) ─────────────────────────────────────────────
                    create(hamburg, rotterdam, "Industrial Goods",      CargoType.MACHINERY),
                    create(hamburg, london,    "Pharmaceutical Batch",  CargoType.MEDICINE),      // MEDIUM risk
                    createSmall(hamburg, newYork, "Watch Collection",   CargoType.LUXURY_GOODS, 10), // cutter-friendly
                    create(hamburg, capeTown,  "Auto Parts",            CargoType.MACHINERY),
                    create(hamburg, dubai,     "Chemical Equipment",    CargoType.MACHINERY),

                    // ── ROTTERDAM (origin) ───────────────────────────────────────────
                    create(rotterdam, london,  "Electronics",           CargoType.ELECTRONICS),
                    create(rotterdam, newYork, "Machinery",             CargoType.MACHINERY),
                    create(rotterdam, capeTown,"Oil Cargo",             CargoType.OIL),
                    createSmall(rotterdam, hamburg, "Flower Shipment",  CargoType.FOOD, 15),      // quick cheap run
                    create(rotterdam, singapore,"Bulk Chemicals",       CargoType.OIL),

                    // ── NEW YORK (origin) ────────────────────────────────────────────
                    create(newYork, losAngeles, "Retail Cargo",         CargoType.CLOTHES),
                    create(newYork, dubai,      "Medical Supplies",     CargoType.MEDICINE),
                    createSmall(newYork, london,"Antiques",             CargoType.LUXURY_GOODS, 10),
                    create(newYork, tokyo,      "Financial Documents",  CargoType.MEDICINE),      // small high-value
                    create(newYork, buenosAires,"Grain Shipment",       CargoType.FOOD),

                    // ── LOS ANGELES (origin) ─────────────────────────────────────────
                    create(losAngeles, buenosAires, "Food Containers",  CargoType.FOOD),
                    create(losAngeles, tokyo,       "High Tech Cargo",  CargoType.ELECTRONICS),
                    createSmall(losAngeles, newYork,"Film Equipment",   CargoType.ELECTRONICS, 18),
                    create(losAngeles, singapore,   "Crude Oil",        CargoType.OIL),
                    create(losAngeles, london,      "Hollywood Merch",  CargoType.LUXURY_GOODS),

                    // ── BUENOS AIRES (origin) ────────────────────────────────────────
                    create(buenosAires, newYork,  "Beef Export",        CargoType.FOOD),
                    create(buenosAires, tokyo,    "Premium Goods",      CargoType.LUXURY_GOODS),
                    create(buenosAires, london,   "Wine Crates",        CargoType.FOOD),
                    createSmall(buenosAires, losAngeles, "Artisan Crafts", CargoType.LUXURY_GOODS, 12),
                    create(buenosAires, capeTown, "Soy Cargo",          CargoType.FOOD),

                    // ── LAGOS (origin) ───────────────────────────────────────────────
                    create(lagos, capeTown, "Cocoa Trade",              CargoType.FOOD),
                    create(lagos, dubai,    "Oil Shipment",             CargoType.OIL),
                    createSmall(lagos, london, "Diamonds",              CargoType.LUXURY_GOODS, 10), // cutter, HIGH risk
                    create(lagos, newYork,  "Crude Oil Barrels",        CargoType.OIL),
                    create(lagos, rotterdam,"Phosphate Rock",           CargoType.MACHINERY),

                    // ── CAPE TOWN (origin) ───────────────────────────────────────────
                    create(capeTown, lagos,  "Mining Equipment",        CargoType.MACHINERY),
                    create(capeTown, dubai,  "Luxury Minerals",         CargoType.LUXURY_GOODS),
                    createSmall(capeTown, london, "Precious Gems",      CargoType.LUXURY_GOODS, 10),
                    create(capeTown, singapore, "Iron Ore",             CargoType.MACHINERY),
                    create(capeTown, rotterdam,"Frozen Fish",           CargoType.FOOD),

                    // ── DUBAI (origin) ───────────────────────────────────────────────
                    create(dubai, singapore, "Fuel Cargo",              CargoType.OIL),
                    create(dubai, london,    "Gold Bullion",            CargoType.LUXURY_GOODS),
                    createSmall(dubai, tokyo,"Spice Parcels",           CargoType.FOOD, 15),
                    create(dubai, newYork,   "Petroleum Products",      CargoType.OIL),
                    create(dubai, rotterdam, "Construction Machinery",  CargoType.MACHINERY),

                    // ── SINGAPORE (origin) ───────────────────────────────────────────
                    create(singapore, tokyo,     "Consumer Electronics", CargoType.ELECTRONICS),
                    create(singapore, dubai,     "Palm Oil Drums",       CargoType.OIL),
                    createSmall(singapore, london,"Rare Spices",         CargoType.LUXURY_GOODS, 12),
                    create(singapore, losAngeles,"Semiconductor Chips",  CargoType.ELECTRONICS),
                    create(singapore, rotterdam, "Rubber Cargo",         CargoType.MACHINERY),

                    // ── TOKYO (origin) ───────────────────────────────────────────────
                    create(tokyo, singapore,  "Tech Parts",             CargoType.ELECTRONICS),
                    create(tokyo, losAngeles, "Car Parts",              CargoType.MACHINERY),
                    createSmall(tokyo, dubai, "Luxury Watches",         CargoType.LUXURY_GOODS, 10),
                    create(tokyo, newYork,    "Consumer Goods",         CargoType.CLOTHES),
                    create(tokyo, london,     "Anime Merchandise",      CargoType.CLOTHES)

            ));
            System.out.println("Cargo loaded safely");
        };
    }

    /**
     * Creates a cargo with a forced small capacity — suitable for the cheap/cutter ship (capacity 50).
     * All other values (price, reward, risk, fuel) are still auto-calculated from type + distance.
     */
    private Cargo createSmall(Port origin, Port dest, String name, CargoType type, int capacity) {
        Cargo c = create(origin, dest, name, type);
        c.setRequiredCapacity(capacity);
        return c;
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