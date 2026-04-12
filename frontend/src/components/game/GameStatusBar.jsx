import GameDay from "../ui/GameDay.jsx";

function GameStatusBar({ session, selectedShip, currentPlayer, myActiveVoyage, voyageProgress }) {
    return (
        <div className="status-bar">
            <p>
                <GameDay tick={session.currentTick} /> |

                {voyageProgress && (
                    <> 🗓️ Day {voyageProgress.current} / {voyageProgress.total} |</>
                )}

                🚢 Ship: {selectedShip?.name || "None"} |
                📍 {selectedShip?.currentPort || "At Sea"}
                💰 Balance: {currentPlayer?.balance ?? "?"}
            </p>

            {myActiveVoyage && (
                <p>
                    ⏳ Traveling {myActiveVoyage.originPort} → {myActiveVoyage.destinationPort}
                </p>
            )}
        </div>
    );
}

export default GameStatusBar;