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
    const myName = storedPlayer?.username;

    const isMe = (p) =>
        p.playerId === myId;

    return (
        <div className={`sidebar ${sidebarOpen ? "open" : "closed"}`}>
            <button
                className="toggle-btn"
                onClick={() => setSidebarOpen(!sidebarOpen)}
            >
                {sidebarOpen ? "❯" : "❮"}
            </button>

            {sidebarOpen && (
                <>
                    <h2 className="sidebar-title">Actions</h2>

                    <button
                        className="action-btn"
                        onClick={() => navigate(`/market/${sessionCode}`)}
                    >
                        Ship Market
                    </button>

                    <button
                        className="action-btn"
                        onClick={() => navigate(`/cargo-offers/${sessionCode}`)}
                    >
                        Cargo Offers
                    </button>

                    <button
                        className="action-btn"
                        onClick={() => navigate(`/company/${sessionCode}`)}
                    >
                        Company
                    </button>

                    <button
                        className="action-btn"
                        onClick={() => navigate(`/voyage/${sessionCode}`)}
                    >
                        Voyage
                    </button>

                    <div className="players-section">

                        {/* 👥 Players mit Status */}
                        <h3>Players</h3>
                        <div className="player-list">
                            {session?.players?.map((p) => (
                                <div key={p.id} className="player-item">
                                    {p.username}{" "}
                                    {p.status === "DISCONNECTED"
                                        ? "(disconnected)"
                                        : "(active)"}
                                </div>
                            ))}
                        </div>

                        {/* 🏆 Leaderboard */}
                        <h3 style={{ marginTop: "15px" }}>🏆 Leaderboard</h3>
                        <div className="player-list">
                            {leaderboard === null ? (
                                <div className="player-item" style={{ opacity: 0.6 }}>
                                    Loading...
                                </div>
                            ) : leaderboard.length === 0 ? (
                                <div className="player-item" style={{ opacity: 0.6 }}>
                                    No scores yet
                                </div>
                            ) : (
                                leaderboard.map((p, index) => (
                                    <div
                                        key={p.playerId ?? p.username}
                                        className="player-item"
                                        style={{
                                            fontWeight: index < 3 ? "bold" : "normal",
                                            fontSize: index < 3 ? "1.05rem" : "0.95rem",
                                            color:
                                                index === 0 ? "gold" :
                                                    index === 1 ? "#c0c0c0" :
                                                        index === 2 ? "#cd7f32" :
                                                            "white",
                                            backgroundColor: isMe(p)
                                                ? "rgba(255,255,255,0.1)"
                                                : "transparent"
                                        }}
                                    >
                                        {p.username && `${getMedal(index)} ${p.username} – ${p.score}`}
                                    </div>
                                ))
                            )}
                        </div>

                    </div>

                    <div className="leave-section">
                        <button
                            className="action-btn leave-btn"
                            onClick={() => handleLeaveSession()}
                        >
                            Leave Session
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}

export default GameSidebar;