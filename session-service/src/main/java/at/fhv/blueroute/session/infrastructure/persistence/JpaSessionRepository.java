package at.fhv.blueroute.session.infrastructure.persistence;

import at.fhv.blueroute.session.domain.model.Session;
import at.fhv.blueroute.session.domain.model.SessionStatus;
import at.fhv.blueroute.session.domain.repository.SessionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaSessionRepository extends JpaRepository<Session, Long>, SessionRepository {
    Optional<Session> findBySessionCode(String sessionCode);
    List<Session> findByStatus(SessionStatus status);
}