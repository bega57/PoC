package at.fhv.blueroute.session.application.service;

import at.fhv.blueroute.common.websocket.SessionStatusMessage;
import at.fhv.blueroute.common.websocket.WebSocketSender;
import at.fhv.blueroute.player.application.exception.PlayerNotFoundException;
import at.fhv.blueroute.player.domain.model.Player;
import at.fhv.blueroute.player.domain.repository.PlayerRepository;
import at.fhv.blueroute.session.application.exception.SessionFullException;
import at.fhv.blueroute.session.application.exception.SessionNotFoundException;
import at.fhv.blueroute.session.application.mapper.SessionMapper;
import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.session.domain.model.SessionPlayerStatus;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.domain.repository.SessionRepository;
import at.fhv.blueroute.session.presentation.dto.SessionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final PlayerRepository playerRepository;
    private final SessionMapper sessionMapper;
    private final WebSocketSender webSocketSender;

    public SessionService(SessionRepository sessionRepository,
                          PlayerRepository playerRepository,
                          SessionMapper sessionMapper,
                          WebSocketSender webSocketSender) {
        this.sessionRepository = sessionRepository;
        this.playerRepository = playerRepository;
        this.sessionMapper = sessionMapper;
        this.webSocketSender = webSocketSender;

    }

    public List<SessionResponse> getAllSessions() {
        return sessionRepository.findAll()
                .stream()
                .map(sessionMapper::toResponse)
                .toList();
    }

    public SessionResponse getSessionByCode(String sessionCode) {
        Session session = sessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new SessionNotFoundException(sessionCode));

        return sessionMapper.toResponse(session);
    }

    public SessionResponse createSession() {
        Session session = new Session(generateSessionCode(), SessionStatus.WAITING, 0, 5);
        Session savedSession = sessionRepository.save(session);
        return sessionMapper.toResponse(savedSession);
    }

    public SessionResponse joinSession(String sessionCode, Long playerId) {
        Session session = sessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new SessionNotFoundException(sessionCode));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        SessionPlayer existingSessionPlayer = session.getSessionPlayerByPlayerId(playerId);

        if (existingSessionPlayer != null) {
            existingSessionPlayer.markActive();
        } else {
            if (session.isFull()) {
                throw new SessionFullException(sessionCode);
            }
            session.addPlayer(player, false);
        }

        if (session.getSessionPlayers().stream().anyMatch(sp -> sp.getStatus() == SessionPlayerStatus.ACTIVE)) {
            session.setStatus(SessionStatus.RUNNING);
        } else {
            session.setStatus(SessionStatus.PAUSED);
        }

        Session updatedSession = sessionRepository.save(session);

        if (updatedSession.getStatus() == SessionStatus.RUNNING) {
            webSocketSender.sendSessionUpdate(
                    updatedSession.getSessionCode(),
                    new SessionStatusMessage(
                            "SESSION_RUNNING",
                            updatedSession.getSessionCode(),
                            updatedSession.getStatus().name()
                    )
            );
        }

        return sessionMapper.toResponse(updatedSession);
    }

    public SessionResponse createSession(Long playerId, int maxPlayers) {
        Player creator = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        SessionStatus status = maxPlayers == 1 ? SessionStatus.RUNNING : SessionStatus.WAITING;

        Session session = new Session(
                generateSessionCode(),
                status,
                0,
                maxPlayers
        );

        session.addPlayer(creator, true);

        Session savedSession = sessionRepository.save(session);
        return sessionMapper.toResponse(savedSession);
    }

    private String generateSessionCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        String sessionCode;
        do {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                builder.append(characters.charAt(random.nextInt(characters.length())));
            }
            sessionCode = builder.toString();
        } while (sessionRepository.findBySessionCode(sessionCode).isPresent());

        return sessionCode;
    }

    public SessionResponse leaveSession(String sessionCode, Long playerId) {
        System.out.println("leaveSession called with sessionCode=" + sessionCode + ", playerId=" + playerId);

        Session session = sessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new SessionNotFoundException(sessionCode));

        System.out.println("session found: " + session.getSessionCode());
        System.out.println("session players: " + session.getSessionPlayers().size());

        SessionPlayer sessionPlayer = session.getSessionPlayerByPlayerId(playerId);

        System.out.println("sessionPlayer found? " + (sessionPlayer != null));

        if (sessionPlayer == null) {
            throw new PlayerNotFoundException(playerId);
        }

        sessionPlayer.markDisconnected();

        boolean hasActivePlayers = session.getSessionPlayers().stream()
                .anyMatch(sp -> sp.getStatus() == SessionPlayerStatus.ACTIVE);

        if (!hasActivePlayers) {
            session.setStatus(SessionStatus.PAUSED);
        }

        Session updatedSession = sessionRepository.save(session);

        if (updatedSession.getStatus() == SessionStatus.PAUSED) {
            webSocketSender.sendSessionUpdate(
                    updatedSession.getSessionCode(),
                    new SessionStatusMessage(
                            "SESSION_PAUSED",
                            updatedSession.getSessionCode(),
                            updatedSession.getStatus().name()
                    )
            );
        }

        return sessionMapper.toResponse(updatedSession);
    }
}