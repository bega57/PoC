package at.fhv.blueroute.session.domain.model;

import at.fhv.blueroute.player.domain.model.Player;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SessionPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Session session;

    @ManyToOne(optional = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    private SessionPlayerStatus status;

    private LocalDateTime joinedAt;
    private LocalDateTime lastSeen;
    private boolean host;

    public SessionPlayer() {
    }

    public SessionPlayer(Session session, Player player, SessionPlayerStatus status, boolean host) {
        this.session = session;
        this.player = player;
        this.status = status;
        this.host = host;
        this.joinedAt = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
    }

    public void markDisconnected() {
        this.status = SessionPlayerStatus.DISCONNECTED;
        this.lastSeen = LocalDateTime.now();
    }

    public void markActive() {
        this.status = SessionPlayerStatus.ACTIVE;
        this.lastSeen = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Session getSession() {
        return session;
    }

    public Player getPlayer() {
        return player;
    }

    public SessionPlayerStatus getStatus() {
        return status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public boolean isHost() {
        return host;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setStatus(SessionPlayerStatus status) {
        this.status = status;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setHost(boolean host) {
        this.host = host;
    }
}