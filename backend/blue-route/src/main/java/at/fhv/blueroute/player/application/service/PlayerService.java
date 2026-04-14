package at.fhv.blueroute.player.application.service;

import at.fhv.blueroute.player.application.exception.PlayerNotFoundException;
import at.fhv.blueroute.player.application.mapper.PlayerMapper;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import at.fhv.blueroute.player.presentation.dto.PlayerRequest;
import at.fhv.blueroute.player.presentation.dto.PlayerResponse;
import at.fhv.blueroute.ship.domain.model.Ship;
import at.fhv.blueroute.ship.infrastructure.persistence.JpaShipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final JpaShipRepository shipRepository;

    public PlayerService(PlayerRepository playerRepository,
                         PlayerMapper playerMapper,
                         JpaShipRepository shipRepository) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
        this.shipRepository = shipRepository;
    }

    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    public PlayerResponse getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));

        return playerMapper.toResponse(player);
    }

    public PlayerResponse createPlayer(PlayerRequest request) {
        Player player = playerMapper.toEntity(request);
        player.setBalance(40000.0);
        player.setLastHeartbeat(LocalDateTime.now());
        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponse(savedPlayer);
    }

    public void selectPort(Long playerId, String port) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        player.setCurrentPort(port);
        playerRepository.save(player);

        List<Ship> ships = shipRepository.findByOwnerId(playerId);

        for (Ship ship : ships) {
            String currentPort = ship.getCurrentPort();

            if (currentPort == null ||
                    currentPort.isBlank() ||
                    currentPort.equalsIgnoreCase("Unknown")) {
                ship.setCurrentPort(port);
            }
        }

        shipRepository.saveAll(ships);
    }

    public void updateHeartbeat(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        player.setLastHeartbeat(LocalDateTime.now());
        playerRepository.save(player);
    }

}