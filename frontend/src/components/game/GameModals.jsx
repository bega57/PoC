import api from "../../api/api.js";

function GameModals({
                        showWelcome,
                        setShowWelcome,
                        selectedPort,
                        setSelectedPort,
                        showPortInstructionModal,
                        setShowPortInstructionModal,
                        showRewardPopup,
                        setShowRewardPopup,
                        rewardAmount,
                        currentPlayer,
                        storedPlayer,
                        selectedShip,
                        setSelectedShip,
                        sessionCode
                    }) {
    return (
        <>
            {showWelcome && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>⚓ Welcome aboard, {currentPlayer?.username || storedPlayer?.username}!</h2>
                        <p>You start with:</p>
                        <h1>40.000 Coins</h1>

                        <button
                            onClick={() => {
                                setShowWelcome(false);
                                setShowPortInstructionModal(true);
                                sessionStorage.setItem(`welcomeShown-${sessionCode}`, "true");
                            }}
                        >
                            Start Playing
                        </button>
                    </div>
                </div>
            )}

            {selectedPort && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>Select {selectedPort} as your main port?</h2>

                        <button
                            className="confirm-btn"
                            onClick={async () => {
                                const existing = localStorage.getItem(`currentPort-${sessionCode}`);

                                if (existing) {
                                    setSelectedPort(null);
                                    return;
                                }

                                const res = await api.post(`/players/select-port`, {
                                    playerId: storedPlayer.id,
                                    port: selectedPort
                                });

                                setSelectedShip(prev => ({
                                    ...(prev || {}),
                                    currentPort: res.data.currentPort
                                }));

                                localStorage.setItem(`currentPort-${sessionCode}`, res.data.currentPort);

                                if (currentPlayer) {
                                    localStorage.setItem(
                                        `player-${sessionCode}`,
                                        JSON.stringify({
                                            ...currentPlayer,
                                            currentPort: res.data.currentPort
                                        })
                                    );
                                }

                                setSelectedPort(null);
                            }}
                        >
                            Confirm
                        </button>

                        <button
                            className="cancel-btn"
                            onClick={() => setSelectedPort(null)}
                        >
                            Cancel
                        </button>
                    </div>
                </div>
            )}

            {showPortInstructionModal && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>🌍 Choose your main port</h2>
                        <p>
                            Click on any red port on the map to select your starting location.
                        </p>

                        <button onClick={() => setShowPortInstructionModal(false)}>
                            Got it
                        </button>
                    </div>
                </div>
            )}

            {showRewardPopup && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>Voyage completed</h2>
                        <p>You successfully completed your transport order.</p>
                        <h1>+{rewardAmount} Coins</h1>

                        <button onClick={() => setShowRewardPopup(false)}>
                            Nice
                        </button>
                    </div>
                </div>
            )}
        </>
    );
}

export default GameModals;