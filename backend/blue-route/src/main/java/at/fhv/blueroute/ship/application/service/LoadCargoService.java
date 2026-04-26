package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import at.fhv.blueroute.port.domain.model.Port;
import at.fhv.blueroute.port.domain.repository.PortRepository;
import at.fhv.blueroute.ship.domain.model.Good;
import at.fhv.blueroute.ship.domain.model.PortGood;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.model.ShipCargo;
import at.fhv.blueroute.ship.infrastructure.persistence.GoodRepository;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import at.fhv.blueroute.ship.infrastructure.persistence.PortGoodRepository;
import at.fhv.blueroute.ship.infrastructure.persistence.ShipCargoRepository;
import at.fhv.blueroute.ship.presentation.dto.LoadCargoRequest;
import org.springframework.stereotype.Service;
import at.fhv.blueroute.common.service.PricingService;

@Service
public class LoadCargoService {

    private final JpaShipRepository shipRepository;
    private final PlayerRepository playerRepository;
    private final PortGoodRepository portGoodRepository;
    private final ShipCargoRepository shipCargoRepository;
    private final GoodRepository goodRepository;
    private final PortRepository portRepository;
    private final PricingService pricingService;

    public LoadCargoService(JpaShipRepository shipRepository,
                            PlayerRepository playerRepository,
                            PortGoodRepository portGoodRepository,
                            ShipCargoRepository shipCargoRepository,
                            GoodRepository goodRepository, PortRepository portRepository, PricingService pricingService) {
        this.shipRepository = shipRepository;
        this.playerRepository = playerRepository;
        this.portGoodRepository = portGoodRepository;
        this.shipCargoRepository = shipCargoRepository;
        this.goodRepository = goodRepository;
        this.portRepository = portRepository;
        this.pricingService = pricingService;
    }

    public void loadCargo(LoadCargoRequest request) {

        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new RuntimeException("Player not found"));

        Ship ship = shipRepository.findById(request.getShipId())
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        if (ship.isTraveling()) {
            throw new RuntimeException("Ship is currently traveling");
        }

        if (!ship.getOwner().getId().equals(player.getId())) {
            throw new RuntimeException("Not your ship");
        }

        Port port = portRepository.findById(request.getPortId())
                .orElseThrow(() -> new RuntimeException("Port not found"));

        if (!ship.getCurrentPort().equalsIgnoreCase(port.getName())) {
            throw new RuntimeException("Ship is not in this port");
        }

        PortGood portGood = portGoodRepository
                .findByPortIdAndGoodId(request.getPortId(), request.getGoodId())
                .orElseThrow(() -> new RuntimeException("Good not available"));

        if (portGood.getStock() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        double basePrice = portGood.getBuyPrice() * request.getQuantity();
        double finalPrice = pricingService.applyVAT(basePrice);

        if (player.getBalance() < finalPrice) {
            throw new RuntimeException("Not enough money");
        }

        Good good = goodRepository.findById(request.getGoodId())
                .orElseThrow();

        double currentLoad = shipCargoRepository.findByShipId(ship.getId())
                .stream()
                .mapToDouble(c -> {
                    Good g = goodRepository.findById(c.getGoodId()).orElseThrow();
                    return c.getQuantity() * g.getWeight();
                })
                .sum();

        double newLoad = request.getQuantity() * good.getWeight();

        if (currentLoad + newLoad > ship.getCargoCapacity()) {
            throw new RuntimeException("Not enough capacity");
        }

        ShipCargo cargo = shipCargoRepository
                .findByShipIdAndGoodId(ship.getId(), good.getId())
                .orElse(null);

        if (cargo == null) {
            cargo = new ShipCargo();
            cargo.setShipId(ship.getId());
            cargo.setGoodId(good.getId());
            cargo.setQuantity(request.getQuantity());
        } else {
            cargo.setQuantity(cargo.getQuantity() + request.getQuantity());
        }

        player.setBalance(player.getBalance() - finalPrice);
        portGood.setStock(portGood.getStock() - request.getQuantity());

        shipCargoRepository.save(cargo);
        portGoodRepository.save(portGood);
        playerRepository.save(player);
    }
}