import "./GameSidebar.css";

function GameSidebar({
                         sidebarOpen,
                         setSidebarOpen,
                         navigate,
                         sessionCode,
                         session,
                         leaderboard,
                         handleLeaveSession
                     }) {
    const getMedal = (i) => {
        if (i === 0) return "🥇";
        if (i === 1) return "🥈";
        if (i === 2) return "🥉";
        return `#${i + 1}`;
    };

    const storedPlayer = JSON.parse(
        sessionStorage.getItem(`player-${sessionCode}`) || "null"
    );
    const myId = storedPlayer?.id;

    const isMe = (p) => p.playerId === myId;

    const isActive = (playerId) => {
        const p = session?.players?.find(pl => pl.id === playerId);
        return p?.status !== "DISCONNECTED";
    };

    return (
        <div className={`sidebar ${sidebarOpen ? "open" : "closed"}`}>
            <button
                className={`toggle-btn ${sidebarOpen ? "open" : "closed"}`}
                onClick={() => setSidebarOpen(prev => !prev)}
            >
                {sidebarOpen ? "›" : "‹"}
            </button>

            {sidebarOpen && (
                <>
                    <h2 className="sidebar-title">Actions</h2>

                    <button className="action-btn" onClick={() => navigate(`/session/${sessionCode}/market`)}>
                        Ship Market
                    </button>
                    <button className="action-btn" onClick={() => navigate(`/session/${sessionCode}/cargo-offers`)}>
                        Cargo Offers
                    </button>
                    <button className="action-btn" onClick={() => navigate(`/session/${sessionCode}/company`)}>
                        Company
                    </button>
                    <button className="action-btn" onClick={() => navigate(`/session/${sessionCode}/voyage`)}>
                        Voyage
                    </button>

                    <div className="divider" />

                    <div className="scoreboard-section">
                        <div className="scoreboard-label">Scoreboard</div>

                        {leaderboard === null ? (
                            <div className="score-row other">
                                <span className="pname" style={{ opacity: 0.5 }}>Loading...</span>
                            </div>
                        ) : leaderboard.length === 0 ? (
                            <div className="score-row other">
                                <span className="pname" style={{ opacity: 0.5 }}>No scores yet</span>
                            </div>
                        ) : (
                            leaderboard.map((p, index) => (
                                <div
                                    key={`${p.playerId ?? p.username ?? "player"}-${index}`}
                                    className={`score-row ${isMe(p) ? "me" : "other"}`}
                                >
                                    <span className={`rank rank-${index}`}>{getMedal(index)}</span>
                                    <span className="pname">
                                        {p.username ?? `Player ${index + 1}`}
                                        {isMe(p) && <span className="you-tag">du</span>}
                                    </span>
                                    <span className={`status-dot ${isActive(p.playerId) ? "active" : "off"}`} />
                                    <span className="pscore">{(p.score ?? 0).toLocaleString("de-DE")} $</span>
                                </div>
                            ))
                        )}
                    </div>

                    <div className="leave-section">
                        <button className="action-btn leave-btn" onClick={() => handleLeaveSession()}>
                            Leave Session
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}

export default GameSidebar;