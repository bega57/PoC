package at.fhv.blueroute.session.domain.model;

import at.fhv.blueroute.player.domain.model.Player;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String sessionCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Column(nullable = false)
    private int currentTick;

    @Column(nullable = false)
    private int maxPlayers;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionPlayer> sessionPlayers = new ArrayList<>();

    public Session() {
    }

    public Session(String sessionCode, SessionStatus status, int currentTick, int maxPlayers) {
        this.sessionCode = sessionCode;
        this.status = status;
        this.currentTick = currentTick;
        this.maxPlayers = maxPlayers;
    }

    public Long getId() {
        return id;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<SessionPlayer> getSessionPlayers() {
        return sessionPlayers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setSessionPlayers(List<SessionPlayer> sessionPlayers) {
        this.sessionPlayers = sessionPlayers;
    }

    public void addPlayer(Player player, boolean host) {
        SessionPlayer sessionPlayer = new SessionPlayer(this, player, SessionPlayerStatus.ACTIVE, host);
        this.sessionPlayers.add(sessionPlayer);
    }

    public boolean hasPlayer(Long playerId) {
        return sessionPlayers.stream()
                .anyMatch(sessionPlayer -> sessionPlayer.getPlayer().getId().equals(playerId));
    }

    public SessionPlayer getSessionPlayerByPlayerId(Long playerId) {
        return sessionPlayers.stream()
                .filter(sessionPlayer -> sessionPlayer.getPlayer().getId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    public int getPlayerCount() {
        return sessionPlayers.size();
    }

    public boolean isFull() {
        return sessionPlayers.size() >= maxPlayers;
    }
}