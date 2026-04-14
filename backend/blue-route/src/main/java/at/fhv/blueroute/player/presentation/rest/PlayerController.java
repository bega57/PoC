package at.fhv.blueroute.player.presentation.rest;

import at.fhv.blueroute.player.application.service.PlayerService;
import at.fhv.blueroute.player.presentation.dto.PlayerRequest;
import at.fhv.blueroute.player.presentation.dto.PlayerResponse;
import at.fhv.blueroute.player.presentation.dto.SelectPortRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerResponse> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    public PlayerResponse getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id);
    }

    @PostMapping
    public PlayerResponse createPlayer(@Valid @RequestBody PlayerRequest request) {
        return playerService.createPlayer(request);
    }

    @PostMapping("/select-port")
    public PlayerResponse selectPort(@RequestBody SelectPortRequest request) {
        playerService.selectPort(request.getPlayerId(), request.getPort());
        return playerService.getPlayerById(request.getPlayerId());
    }

    @PatchMapping("/{playerId}/heartbeat")
    public ResponseEntity<Void> heartbeat(@PathVariable Long playerId) {
        playerService.updateHeartbeat(playerId);
        return ResponseEntity.noContent().build();
    }
}