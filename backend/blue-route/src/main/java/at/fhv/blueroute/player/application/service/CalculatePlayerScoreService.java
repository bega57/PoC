package at.fhv.blueroute.player.application.service;

import at.fhv.blueroute.player.domain.model.Player;
import org.springframework.stereotype.Service;

@Service
public class CalculatePlayerScoreService {

    public int calculateScore(Player player) {
        if (player.getBalance() == null) {
            return 0;
        }
        return player.getBalance().intValue();
    }
}
