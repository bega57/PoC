import api from "../../api/api.js";
import RetroModal from "../ui/RetroModal";

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
                        finishedVoyageInfo,
                        setFinishedVoyageInfo,
                        currentPlayer,
                        storedPlayer,
                        selectedShip,
                        setSelectedShip,
                        sessionCode,
                        showLeaveModal,
                        setShowLeaveModal,
                        handleLeaveSession,
                        closeLeaveModal,
                        setSession
                    }) {
    return (
        <>
            {showWelcome && (
                <RetroModal
                    title="Welcome"
                    onClose={() => setShowWelcome(false)}
                >
                        <h2>⚓ Welcome aboard, {currentPlayer?.username || storedPlayer?.username}!</h2>
                        <p>You start with:</p>
                        <h1>40.000 Coins</h1>

                        <button
                            className="retro-button"
                            onClick={() => {
                                setShowWelcome(false);
                                setShowPortInstructionModal(true);
                                sessionStorage.setItem(`welcomeShown-${sessionCode}`, "true");
                            }}
                        >
                            Start Playing
                        </button>
                </RetroModal>
            )}

            {selectedPort && (
                <RetroModal
                    title="Select Main Port"
                    onClose={() => setSelectedPort(null)}
                >
                        <h2>Select {selectedPort} as your main port?</h2>

                        <button
                            className="retro-button"
                            onClick={async () => {
                                const existing = sessionStorage.getItem(`currentPort-${sessionCode}`);

                                if (existing) {
                                    setSelectedPort(null);
                                    return;
                                }

                                const res = await api.post(`/players/select-port`, {
                                    playerId: storedPlayer.id,
                                    port: selectedPort
                                });

                                setSession(prev => {
                                    if (!prev) return prev;

                                    return {
                                        ...prev,
                                        players: prev.players.map(p => {
                                            if (p.id !== storedPlayer.id) return p;

                                            return {
                                                ...p,
                                                ships: p.ships.map(ship => ({
                                                    ...ship,
                                                    currentPort: res.data.currentPort
                                                }))
                                            };
                                        })
                                    };
                                });

                                setSelectedShip(prev => ({
                                    ...(prev || {}),
                                    currentPort: res.data.currentPort
                                }));

                                sessionStorage.setItem(`currentPort-${sessionCode}`, res.data.currentPort);

                                setSelectedPort(null);
                            }}
                        >
                            Confirm
                        </button>

                    <button className="retro-button secondary">
                        Cancel
                    </button>
                </RetroModal>
            )}

            {showPortInstructionModal && (
                <RetroModal
                    title="Main Port"
                    onClose={() => setShowPortInstructionModal(false)}
                >
                        <h2>🌍 Choose your main port</h2>
                        <p>
                            Click on any red port on the map to select your starting location.
                        </p>

                        <button
                            className="retro-button"
                            onClick={() => setShowPortInstructionModal(false)}>
                            Got it
                        </button>
                </RetroModal>
            )}

            {showRewardPopup && (
                <RetroModal
                    title="Voyage Complete"
                    onClose={() => {
                        setShowRewardPopup(false);
                        setFinishedVoyageInfo(null);
                    }}
                >
                        <p>You successfully completed your transport order.</p>

                    <h1 className="reward-amount">
                        +{Number(rewardAmount).toFixed(0)} Coins
                    </h1>

                        {finishedVoyageInfo?.eventResultMessage && (
                            <div className="voyage-finish-event-box">
                                <h3>Event result</h3>
                                <p>{finishedVoyageInfo.eventResultMessage}</p>

                                {finishedVoyageInfo.eventCost > 0 && (
                                    <p>Bribe / event cost: -{finishedVoyageInfo.eventCost} Coins</p>
                                )}

                                {finishedVoyageInfo.extraDelayTicks > 0 && (
                                    <p>Delay: +{finishedVoyageInfo.extraDelayTicks} days</p>
                                )}

                                {finishedVoyageInfo.extraFuelLoss > 0 && (
                                    <p>Extra fuel used: -{finishedVoyageInfo.extraFuelLoss}</p>
                                )}

                                {finishedVoyageInfo.extraConditionLoss > 0 && (
                                    <p>Extra condition loss: -{finishedVoyageInfo.extraConditionLoss}</p>
                                )}

                                {finishedVoyageInfo.rewardLossPercent > 0 && (
                                    <p>Reward reduced by {finishedVoyageInfo.rewardLossPercent}%</p>
                                )}
                            </div>
                        )}

                        <button
                            className="retro-button"
                            onClick={() => {
                                setShowRewardPopup(false);
                                setFinishedVoyageInfo(null);
                            }}
                        >
                            Nice
                        </button>
                </RetroModal>
            )}

            {showLeaveModal && (
                <RetroModal
                    title="Leave Session"
                    onClose={() => setShowLeaveModal(false)}
                >
                        <p>
                            You can resume this session later with the following details:
                        </p>

                        <p><strong>Session Code:</strong> {sessionCode}</p>
                        <p><strong>Player ID:</strong> {storedPlayer?.id}</p>

                        <p>
                            Make sure to save this information before leaving.
                        </p>

                        <button
                            className="retro-button"
                            onClick={async () => {
                                localStorage.setItem(`sessionCode-${storedPlayer.id}`, sessionCode);
                                localStorage.setItem("lastSessionCode", sessionCode);
                                localStorage.setItem("lastPlayerId", storedPlayer.id);

                                await handleLeaveSession();
                                closeLeaveModal();
                            }}
                        >
                            Leave Session
                        </button>

                    <button
                        className="retro-button secondary"
                        onClick={() => setShowLeaveModal(false)}
                    >
                        Cancel
                    </button>
                </RetroModal>
            )}
        </>
    );
}

export default GameModals;