import GameDay from "../ui/GameDay.jsx";

function GameStatusBar({ session, currentPlayer, myActiveVoyages }) {

    const allShips = session.players.flatMap(p => p.ships || []);

    return (
        <div className="status-bar">

            {/* TOP LINE */}
            <p>
                <GameDay tick={session.currentTick} />
                {"  "}💰 {currentPlayer?.balance ?? "?"}
            </p>

            {/* SHIPS */}
            {myActiveVoyages.length > 0 ? (
                myActiveVoyages.map(v => {
                    const ship = allShips.find(s => s.id === v.shipId);

                    return (
                        <div key={v.id} style={{ marginTop: "8px" }}>

                            <p>🚢 Ship: {ship?.name || v.shipId}</p>

                            <p>
                                ⏳ Traveling {v.originPort} → {v.destinationPort}
                            </p>

                            <p>
                                🗓️ Day {v.currentDay} / {v.duration}
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