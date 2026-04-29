package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.player.domain.model.Player;
import org.springframework.stereotype.Service;

@Service
public class RepairShipService {

    private final ShipRepository shipRepository;
    private final ShipMapper shipMapper;

    public RepairShipService(ShipRepository shipRepository, ShipMapper shipMapper) {
        this.shipRepository = shipRepository;
        this.shipMapper = shipMapper;
    }

    public ShipResponse repair(Long shipId) {
        Ship ship = shipRepository.findById(shipId)
                .orElseThrow(() -> new RuntimeException("Ship not found"));

        Player player = ship.getOwner();

        double maxCondition = 100.0;
        double currentCondition = ship.getCondition();

        double missing = maxCondition - currentCondition;

        if (missing <= 0) {
            throw new RuntimeException("Ship already fully repaired");
        }

        double pricePerUnit = 0.5;
        double cost = missing * pricePerUnit;

        if (player.getBalance() < cost) {
            throw new RuntimeException("Not enough money");
        }

        player.setBalance(player.getBalance() - cost);


        ship.setCondition(maxCondition);

        Ship saved = shipRepository.save(ship);

        return shipMapper.toResponse(saved, 0, 0);
    }
}