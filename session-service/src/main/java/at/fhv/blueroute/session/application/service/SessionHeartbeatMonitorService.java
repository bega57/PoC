package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.websocket.dto.SessionStatusMessage;
import at.fhv.blueroute.session.backend.client.BackendWebSocketClient;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.session.domain.model.SessionPlayerStatus;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionPlayerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessionHeartbeatMonitorService {

    private static final Logger log = LoggerFactory.getLogger(SessionHeartbeatMonitorService.class);

    private final JpaSessionPlayerRepository sessionPlayerRepository;
    private final BackendWebSocketClient backendWebSocketClient;

    @Value("${session.heartbeat.timeout-seconds:120}")
    private long heartbeatTimeoutSeconds;

    public SessionHeartbeatMonitorService(JpaSessionPlayerRepository sessionPlayerRepository,
                                         BackendWebSocketClient backendWebSocketClient) {
        this.sessionPlayerRepository = sessionPlayerRepository;
        this.backendWebSocketClient = backendWebSocketClient;
    }

    @Scheduled(fixedRate = 30000)
    public void checkHeartbeats() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(heartbeatTimeoutSeconds);

        List<SessionPlayer> activePlayers = sessionPlayerRepository.findByStatusAndSession_Status(
                SessionPlayerStatus.ACTIVE,
                SessionStatus.RUNNING
        );

        for (SessionPlayer sessionPlayer : activePlayers) {
            LocalDateTime safeCutoff = cutoff.minusSeconds(10);

            log.info(
                    "Heartbeat check - Player {} | LastSeen: {} | Now: {} | Diff: {} sec",
                    sessionPlayer.getPlayerId(),
                    sessionPlayer.getLastSeen(),
                    LocalDateTime.now(),
                    sessionPlayer.getLastSeen() != null
                            ? java.time.Duration.between(sessionPlayer.getLastSeen(), LocalDateTime.now()).getSeconds()
                            : "null"
            );

            if (sessionPlayer.getLastSeen() != null && sessionPlayer.getLastSeen().isBefore(safeCutoff)) {
                sessionPlayer.markDisconnected();

                log.info(
                        "Player {} disconnected due to heartbeat timeout in session {}",
                        sessionPlayer.getPlayerId(),
                        sessionPlayer.getSession().getSessionCode()
                );

                Session session = sessionPlayer.getSession();
                List<SessionPlayer> playersInSession =
                        sessionPlayerRepository.findBySessionId(session.getId());

                boolean hasActivePlayer = playersInSession.stream()
                        .anyMatch(player -> player.getStatus() == SessionPlayerStatus.ACTIVE);

                if (!hasActivePlayer) {
                    session.setStatus(SessionStatus.PAUSED);

                    backendWebSocketClient.publish(
                            session.getSessionCode(),
                            new SessionStatusMessage(
                                    "SESSION_PAUSED",
                                    session.getSessionCode(),
                                    session.getStatus().name()
                            )
                    );
                }
            }
        }
    }
}