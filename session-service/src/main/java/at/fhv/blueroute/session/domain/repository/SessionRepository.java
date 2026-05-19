package at.fhv.blueroute.session.domain.repository;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    List<Session> findAll();
    Optional<Session> findBySessionCode(String sessionCode);
    Session save(Session session);
    List<Session> findByStatus(SessionStatus status);
    Optional<Session> findById(Long id);
}