function GameStatusBar({ session, selectedShip, currentPlayer, myActiveVoyage }) {
    return (
        <div className="status-bar">
            <p>
                🕒 Game Day: {session.currentTick} |
                🚢 Ship: {selectedShip?.name || "None"} |
                📍 {currentPlayer?.currentPort || "No Port"} |
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