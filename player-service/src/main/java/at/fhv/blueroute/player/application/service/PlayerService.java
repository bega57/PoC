package at.fhv.blueroute.player.application.service;

import at.fhv.blueroute.player.application.exception.InsufficientBalanceException;
import at.fhv.blueroute.player.application.exception.PlayerNotFoundException;
import at.fhv.blueroute.player.application.mapper.PlayerMapper;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.infrastructure.persistence.JpaPlayerRepository;
import at.fhv.blueroute.player.presentation.dto.PlayerRequest;
import at.fhv.blueroute.player.presentation.dto.PlayerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final JpaPlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerService(JpaPlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    public PlayerResponse getPlayerById(Long id) {
        Player player = findPlayer(id);
        return playerMapper.toResponse(player);
    }

    public PlayerResponse createPlayer(PlayerRequest request) {
        Player player = playerMapper.toEntity(request);
        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponse(savedPlayer);
    }

    public PlayerResponse selectPort(Long playerId, String port) {
        Player player = findPlayer(playerId);
        player.setCurrentPort(port);
        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponse(savedPlayer);
    }

    public PlayerResponse updateBalance(Long playerId, Double amount, String reason) {
        Player player = findPlayer(playerId);

        double newBalance = player.getBalance() + amount;

        if (newBalance < 0) {
            throw new InsufficientBalanceException(playerId);
        }

        player.setBalance(newBalance);
        Player savedPlayer = playerRepository.save(player);

        return playerMapper.toResponse(savedPlayer);
    }

    private Player findPlayer(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public PlayerResponse updateCompanyName(Long playerId, String companyName) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name is required.");
        }

        player.setCompanyName(companyName.trim());

        return playerMapper.toResponse(playerRepository.save(player));
    }
}