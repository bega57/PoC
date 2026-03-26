package at.fhv.blueroute.ship.application.service;

import at.fhv.blueroute.player.application.exception.PlayerNotFoundException;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import at.fhv.blueroute.ship.application.exception.InsufficientBalanceException;
import at.fhv.blueroute.ship.application.mapper.ShipMapper;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.domain.model.ShipType;
import at.fhv.blueroute.ship.domain.repository.ShipRepository;
import at.fhv.blueroute.ship.presentation.dto.BuyShipRequest;
import at.fhv.blueroute.ship.presentation.dto.ShipResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipService {

    private final ShipRepository shipRepository;
    private final PlayerRepository playerRepository;
    private final ShipMapper shipMapper;

    public ShipService(ShipRepository shipRepository,
                       PlayerRepository playerRepository,
                       ShipMapper shipMapper) {
        this.shipRepository = shipRepository;
        this.playerRepository = playerRepository;
        this.shipMapper = shipMapper;
    }

    public ShipResponse buyShip(BuyShipRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(request.getPlayerId()));

        if (request.getShipName() == null || request.getShipName().isBlank()) {
            throw new IllegalArgumentException("Ship name is required.");
        }

        ShipType shipType = ShipType.valueOf(request.getShipType().toUpperCase());

        double price = shipType.getPrice();
        int capacity = shipType.getCapacity();
        int speed = shipType.getSpeed();

        if (player.getBalance() < price) {
            throw new InsufficientBalanceException(player.getId());
        }

        boolean firstShip = shipRepository.findByOwnerId(player.getId()).isEmpty();

        if (firstShip && (request.getCompanyName() == null || request.getCompanyName().isBlank())) {
            throw new IllegalArgumentException("Company name is required for the first ship.");
        }

        if (firstShip) {
            player.setCompanyName(request.getCompanyName().trim());
        }

        player.setBalance(player.getBalance() - price);

        Ship ship = new Ship(
                request.getShipName().trim(),
                shipType,
                price,
                capacity,
                speed,
                player
        );

        playerRepository.save(player);
        Ship savedShip = shipRepository.save(ship);

        return shipMapper.toResponse(savedShip);
    }

    public List<ShipResponse> getShipsByPlayer(Long playerId) {
        return shipRepository.findByOwnerId(playerId)
                .stream()
                .map(shipMapper::toResponse)
                .toList();
    }
}