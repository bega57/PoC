package at.fhv.blueroute.player.presentation.rest;

import at.fhv.blueroute.player.client.PlayerServiceClient;
import at.fhv.blueroute.player.client.dto.PlayerResponse;
import at.fhv.blueroute.player.client.dto.UpdateCompanyNameRequest;
import at.fhv.blueroute.player.presentation.dto.PlayerRequest;
import at.fhv.blueroute.player.presentation.dto.SelectPortRequest;
import at.fhv.blueroute.ship.client.ShipServiceClient;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerServiceClient playerServiceClient;
    private final ShipServiceClient shipServiceClient;

    public PlayerController(
            PlayerServiceClient playerServiceClient,
            ShipServiceClient shipServiceClient
    ) {
        this.playerServiceClient = playerServiceClient;
        this.shipServiceClient = shipServiceClient;
    }

    @GetMapping
    public List<PlayerResponse> getAllPlayers() {
        return playerServiceClient.getAllPlayers();
    }

    @GetMapping("/{id}")
    public PlayerResponse getPlayerById(@PathVariable Long id) {
        return playerServiceClient.getPlayer(id);
    }

    @PostMapping
    public PlayerResponse createPlayer(@Valid @RequestBody PlayerRequest request) {
        return playerServiceClient.createPlayer(request);
    }

    @PostMapping("/select-port")
    public PlayerResponse selectPort(@RequestBody SelectPortRequest request) {
        PlayerResponse player = playerServiceClient.selectPort(request.getPlayerId(), request.getPort());
        shipServiceClient.assignPortToUnlocatedShips(request.getPlayerId(), request.getPort());
        return player;
    }

    @PatchMapping("/{playerId}/heartbeat")
    public ResponseEntity<Void> heartbeat(@PathVariable Long playerId) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playerId}/company-name")
    public PlayerResponse updateCompanyName(
            @PathVariable Long playerId,
            @RequestBody UpdateCompanyNameRequest request
    ) {
        return playerServiceClient.updateCompanyName(
                playerId,
                request.getCompanyName()
        );
    }
}