package at.fhv.blueroute.player.application.service;

import at.fhv.blueroute.player.domain.model.Player;
import org.springframework.stereotype.Service;

@Service
public class CalculatePlayerScoreService {

    public int calculateScore(Player player) {
        return player.getBalance().intValue();
    }
}
