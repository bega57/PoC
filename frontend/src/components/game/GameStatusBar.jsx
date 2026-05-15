import GameDay from "../ui/GameDay.jsx";

function GameStatusBar({ session, currentPlayer, myActiveVoyages }) {

    const allShips = session.players.flatMap(p => p.ships || []);

    return (
        <div className="status-bar">

            <div className="status-header">
                Fleet Status
                <span style={{ marginLeft: "6px", opacity: 0.6 }}>
                    ({myActiveVoyages.length})
                </span>
            </div>

            {/* SHIPS */}
            {myActiveVoyages.length > 0 ? (
                myActiveVoyages.map(v => {
                    const ship = allShips.find(s => s.id === v.shipId);

                    const progress = v.progress ?? 0;

                    const currentDay =
                        Math.max(1, v.currentDay ?? 1);

                    return (
                        <div key={v.id} style={{ marginTop: "8px" }}>

                            <p>🚢 Ship: {ship?.name || v.shipId}</p>

                            <p>
                                ⏳ Traveling {v.originPort} → {v.destinationPort}
                            </p>

                            <div style={{
                                width: "100%",
                                height: "10px",
                                background: "#1f2937",
                                borderRadius: "6px",
                                overflow: "hidden",
                                marginTop: "4px"
                            }}>
                                <div style={{
                                    width: `${progress * 100}%`,
                                    height: "100%",
                                    background: "linear-gradient(90deg, #22c55e, #4ade80)",
                                    boxShadow: "0 0 6px #22c55e",
                                    transition: "width 0.3s linear"
                                }} />
                            </div>

                            <p style={{ fontSize: "12px", opacity: 0.7 }}>
                                Day {currentDay} / {v.duration}
                            </p>

                        </div>
                    );
                })
            ) : (
                <p style={{ marginTop: "8px", opacity: 0.6 }}>
                    No ships traveling
                </p>
            )}

        </div>
    );
}

export default GameStatusBar;