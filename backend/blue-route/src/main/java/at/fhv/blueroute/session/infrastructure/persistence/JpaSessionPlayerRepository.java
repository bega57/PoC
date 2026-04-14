package at.fhv.blueroute.session.infrastructure.persistence;

import at.fhv.blueroute.session.domain.model.SessionPlayer;
import at.fhv.blueroute.session.domain.model.SessionPlayerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSessionPlayerRepository extends JpaRepository<SessionPlayer, Long> {

    List<SessionPlayer> findByStatus(SessionPlayerStatus status);

    List<SessionPlayer> findBySessionId(Long sessionId);
}