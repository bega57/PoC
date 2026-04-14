package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.session.domain.model.SessionPlayerStatus;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionPlayerRepository;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import at.fhv.blueroute.common.websocket.SessionStatusMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SessionHeartbeatMonitorService {

    private static final Logger log = LoggerFactory.getLogger(SessionHeartbeatMonitorService.class);

    private final JpaSessionPlayerRepository sessionPlayerRepository;
    private final JpaSessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;

    @Value("${session.heartbeat.timeout-seconds:120}")
    private long heartbeatTimeoutSeconds;

    public SessionHeartbeatMonitorService(JpaSessionPlayerRepository sessionPlayerRepository,
                                          JpaSessionRepository sessionRepository,
                                          WebSocketSender webSocketSender) {
        this.webSocketSender = webSocketSender;
        this.sessionPlayerRepository = sessionPlayerRepository;
        this.sessionRepository = sessionRepository;
    }

    @Scheduled(fixedRate = 30000)
    public void checkHeartbeats() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(heartbeatTimeoutSeconds);

        List<SessionPlayer> activePlayers = sessionPlayerRepository.findByStatusAndSession_Status(
                SessionPlayerStatus.ACTIVE,
                SessionStatus.RUNNING
        );

        for (SessionPlayer sessionPlayer : activePlayers) {
            if (sessionPlayer.getLastSeen() != null && sessionPlayer.getLastSeen().isBefore(cutoff)) {
                sessionPlayer.markDisconnected();

                log.info(
                        "Player {} disconnected due to heartbeat timeout in session {}",
                        sessionPlayer.getPlayer().getId(),
                        sessionPlayer.getSession().getSessionCode()
                );

                Session session = sessionPlayer.getSession();
                List<SessionPlayer> playersInSession =
                        sessionPlayerRepository.findBySessionId(session.getId());

                boolean hasActivePlayer = playersInSession.stream()
                        .anyMatch(player -> player.getStatus() == SessionPlayerStatus.ACTIVE);

                if (!hasActivePlayer) {
                    session.setStatus(SessionStatus.PAUSED);

                    webSocketSender.sendSessionUpdate(
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