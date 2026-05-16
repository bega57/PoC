package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.session.common.websocket.SessionUpdateMessage;
import at.fhv.blueroute.session.common.websocket.WebSocketSender;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.domain.repository.SessionRepository;
import at.fhv.blueroute.session.infrastructure.persistence.JpaSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SessionTickService {

    private final SessionRepository sessionRepository;
    private final WebSocketSender webSocketSender;

    public SessionTickService(SessionRepository sessionRepository,
                              WebSocketSender webSocketSender) {
        this.sessionRepository = sessionRepository;
        this.webSocketSender = webSocketSender;
    }

    public void processTicks() {
        List<Session> runningSessions = sessionRepository.findByStatus(SessionStatus.RUNNING);

        for (Session session : runningSessions) {
            session.setCurrentTick(session.getCurrentTick() + 1);
            sessionRepository.save(session);

            webSocketSender.sendSessionUpdate(
                    session.getSessionCode(),
                    new SessionUpdateMessage(
                            "TICK",
                            session.getSessionCode(),
                            session.getCurrentTick()
                    )
            );
        }
    }
}