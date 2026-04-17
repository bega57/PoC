function GameSidebar({
                         sidebarOpen,
                         setSidebarOpen,
                         navigate,
                         sessionCode,
                         session,
                         handleLeaveSession
                     }) {
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
                        <h3>Players</h3>
                        <div className="player-list">
                            {session.players.map((p) => (
                                <div key={p.id} className="player-item">
                                    {p.username} {p.status === "DISCONNECTED" ? "(disconnected)" : "(active)"}
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="leave-section">
                        <button
                            className="action-btn leave-btn"
                            onClick={handleLeaveSession}
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