package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.session.domain.model.SessionPlayerStatus;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionPlayerRepository;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessionHeartbeatMonitorService {

    private final JpaSessionPlayerRepository sessionPlayerRepository;
    private final JpaSessionRepository sessionRepository;

    @Value("${session.heartbeat.timeout-seconds:120}")
    private long heartbeatTimeoutSeconds;

    public SessionHeartbeatMonitorService(JpaSessionPlayerRepository sessionPlayerRepository,
                                          JpaSessionRepository sessionRepository) {
        this.sessionPlayerRepository = sessionPlayerRepository;
        this.sessionRepository = sessionRepository;
    }

    @Scheduled(fixedRate = 30000)
    public void checkHeartbeats() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(heartbeatTimeoutSeconds);

        List<SessionPlayer> activePlayers = sessionPlayerRepository.findByStatus(SessionPlayerStatus.ACTIVE);

        for (SessionPlayer sessionPlayer : activePlayers) {
            if (sessionPlayer.getLastSeen() != null && sessionPlayer.getLastSeen().isBefore(cutoff)) {
                sessionPlayer.markDisconnected();

                Session session = sessionPlayer.getSession();
                List<SessionPlayer> playersInSession =
                        sessionPlayerRepository.findBySessionId(session.getId());

                boolean hasActivePlayer = playersInSession.stream()
                        .anyMatch(player -> player.getStatus() == SessionPlayerStatus.ACTIVE);

                if (!hasActivePlayer) {
                    session.setStatus(SessionStatus.PAUSED);
                                }
            }
        }
    }
}