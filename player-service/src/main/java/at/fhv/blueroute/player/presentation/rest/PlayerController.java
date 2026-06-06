package at.fhv.blueroute.player.presentation.rest;

import at.fhv.blueroute.player.application.service.PlayerService;
import at.fhv.blueroute.player.presentation.dto.*;
import at.fhv.blueroute.player.presentation.dto.LeaderboardEntryResponse;
import jakarta.validation.Valid;
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

    @PostMapping("/{playerId}/port")
    public PlayerResponse selectPort(
            @PathVariable Long playerId,
            @Valid @RequestBody SelectPortRequest request
    ) {
        return playerService.selectPort(playerId, request.getPort());
    }

    @PostMapping("/{playerId}/balance")
    public PlayerResponse updateBalance(
            @PathVariable Long playerId,
            @Valid @RequestBody BalanceUpdateRequest request
    ) {
        return playerService.updateBalance(
                playerId,
                request.getAmount(),
                request.getReason()
        );
    }

    @PostMapping("/{playerId}/company-name")
    public PlayerResponse updateCompanyName(
            @PathVariable Long playerId,
            @Valid @RequestBody UpdateCompanyNameRequest request
    ) {
        return playerService.updateCompanyName(playerId, request.getCompanyName());
    }

    @PostMapping("/{playerId}/points")
    public PlayerResponse addPoints(
            @PathVariable Long playerId,
            @RequestBody PointsUpdateRequest request
    ) {
        return playerService.addPoints(playerId, request.getAmount());
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntryResponse> getLeaderboard(
            @RequestParam String sessionCode
    ) {
        return playerService.getLeaderboard(sessionCode);
    }
}