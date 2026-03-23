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

    @ManyToMany
    @JoinTable(
            name = "session_players",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players = new ArrayList<>();

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

    public List<Player> getPlayers() {
        return players;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public boolean hasPlayer(Long playerId) {
        return players.stream().anyMatch(player -> player.getId().equals(playerId));
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }
}