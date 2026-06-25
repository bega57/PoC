import api from "../../api/api.js";
import RetroModal from "../ui/RetroModal";
import { useState } from "react";

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
                        leaderboard,
                        setSession,
                        onDataRefresh
                    }) {

    // ==================== SMUGGLING STATE ====================
    const [smugglingResult, setSmugglingResult] = useState(null);
    const [smugglingLoading, setSmugglingLoading] = useState(false);
    // =========================================================

    const handleBribe = async (bribe) => {
        if (!finishedVoyageInfo?.voyageId) return;

        setSmugglingLoading(true);
        try {
            const res = await api.post(
                `/voyages/${finishedVoyageInfo.voyageId}/smuggling-resolve?bribe=${bribe}`
            );
            setSmugglingResult(res.data);
            if (onDataRefresh) onDataRefresh();
        } catch (err) {
            console.error("Failed to resolve smuggling:", err);
        } finally {
            setSmugglingLoading(false);
        }
    };

    // Check if we need to show customs dialog
    const needsCustomsDialog =
        finishedVoyageInfo?.smugglingDetected &&
        !finishedVoyageInfo?.smugglingResolved &&
        !smugglingResult;

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
                    <p>Set <strong>{selectedPort}</strong> as your main port?</p>

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
                                            ships: (p.ships ?? []).map(ship => ({
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
                            if (onDataRefresh) onDataRefresh();
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
                        if (needsCustomsDialog) return;
                        setShowRewardPopup(false);
                        setFinishedVoyageInfo(null);
                        setSmugglingResult(null);
                    }}
                >
                    <p>You successfully completed your transport order.</p>

                    <h1 className="reward-amount">
                        +{Number(rewardAmount).toFixed(0)} Coins
                    </h1>

                    {finishedVoyageInfo?.earnedPoints > 0 && (
                        <div className="voyage-finish-event-box" style={{ borderColor: "#facc15" }}>
                            <h3>⭐ +{finishedVoyageInfo.earnedPoints} Points earned</h3>
                            {finishedVoyageInfo.pointsBreakdown && (
                                <p style={{ fontSize: "12px", opacity: 0.75, marginTop: "4px" }}>
                                    {finishedVoyageInfo.pointsBreakdown}
                                </p>
                            )}
                        </div>
                    )}

                    {finishedVoyageInfo?.eventResultMessage && !needsCustomsDialog && !smugglingResult && (
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

                    {/* ========== SMUGGLING: successful smuggling ========== */}
                    {finishedVoyageInfo?.smuggling && !finishedVoyageInfo?.smugglingDetected && (
                        <div className="voyage-finish-event-box" style={{ borderColor: "#22c55e" }}>
                            <h3>🤫 Smuggling Successful!</h3>
                            <p>Your illegal cargo was not detected.</p>
                            <p style={{ color: "#22c55e", fontWeight: "bold" }}>
                                +{Number(finishedVoyageInfo.smugglingReward).toFixed(0)} Coins bonus
                            </p>
                        </div>
                    )}

                    {/* ========== SMUGGLING: customs passed, no smuggling ========== */}
                    {finishedVoyageInfo?.customsChecked && !finishedVoyageInfo?.smuggling && (
                        <div className="voyage-finish-event-box">
                            <h3>🛃 Customs Inspection</h3>
                            <p>Customs checked your ship. Nothing illegal found.</p>
                        </div>
                    )}

                    {/* ========== SMUGGLING: caught - bribe dialog ========== */}
                    {needsCustomsDialog && (
                        <div className="voyage-finish-event-box" style={{ borderColor: "#ef4444" }}>
                            <h3>🚨 Smuggling Detected!</h3>
                            <p>Customs officers found your illegal cargo!</p>
                            <p>Fine: {Number(finishedVoyageInfo.smugglingPenalty).toFixed(0)} Coins</p>
                            <p>Ship detained for: {finishedVoyageInfo.smugglingDetentionTicks} days</p>
                            <p style={{ marginTop: "10px" }}>
                                You can try to bribe the officer (50/50 chance).
                                If rejected, the fine doubles!
                            </p>

                            <button
                                className="retro-button"
                                onClick={() => handleBribe(true)}
                                disabled={smugglingLoading}
                            >
                                💰 Try to Bribe
                            </button>

                            <button
                                className="retro-button secondary"
                                onClick={() => handleBribe(false)}
                                disabled={smugglingLoading}
                            >
                                Accept Fine
                            </button>
                        </div>
                    )}

                    {/* ========== SMUGGLING: bribe result ========== */}
                    {smugglingResult && (
                        <div className="voyage-finish-event-box"
                             style={{ borderColor: smugglingResult.smugglingPenalty > 0 ? "#ef4444" : "#22c55e" }}
                        >
                            <h3>
                                {smugglingResult.smugglingPenalty > 0 ? "😬" : "😎"}{" "}
                                Smuggling Outcome
                            </h3>
                            <p>{smugglingResult.eventResultMessage}</p>
                            {smugglingResult.smugglingPenalty > 0 && (
                                <p style={{ color: "#ef4444" }}>
                                    Penalty: -{Number(smugglingResult.smugglingPenalty).toFixed(0)} Coins
                                </p>
                            )}
                        </div>
                    )}

                    {finishedVoyageInfo?.activePowerUp && (() => {
                        const pu = finishedVoyageInfo.activePowerUp;
                        let msg = null;
                        if (pu === "LUCKY_CLOVER" && !finishedVoyageInfo.customsChecked)
                            msg = { icon: "🍀", text: "Lucky Clover was active — customs didn't inspect your ship!" };
                        else if (pu === "TURBO_CABLE" && finishedVoyageInfo.extraDelayTicks > 0)
                            msg = { icon: "⚡", text: "Turbo Cable absorbed the delay — no extra days added!" };
                        else if (pu === "TURBO_CABLE")
                            msg = { icon: "⚡", text: "Turbo Cable was ready — no delays occurred anyway." };
                        else if (pu === "CHOCOLATE_CAKE")
                            msg = { icon: "🍰", text: "Chocolate Cake gave your crew a boost — 50% bonus points earned!" };
                        else if (pu === "RED_BULL")
                            msg = { icon: "🐂", text: "Red Bull shortened your voyage by 1 day!" };
                        else if (pu === "VIP_PASS")
                            msg = { icon: "💎", text: "VIP Pass granted priority docking — 20% extra reward!" };
                        if (!msg) return null;
                        return (
                            <div className="voyage-finish-event-box" style={{ borderColor: "#a855f7" }}>
                                <h3>{msg.icon} Power-Up Effect</h3>
                                <p>{msg.text}</p>
                            </div>
                        );
                    })()}

                    {!needsCustomsDialog && (
                        <button
                            className="retro-button"
                            onClick={() => {
                                setShowRewardPopup(false);
                                setFinishedVoyageInfo(null);
                                setSmugglingResult(null);
                            }}
                        >
                            Nice
                        </button>
                    )}
                </RetroModal>
            )}

            {showLeaveModal && (
                <RetroModal
                    title="Leave Session"
                    onClose={() => setShowLeaveModal(false)}
                >
                    {/* Leaderboard */}
                    {leaderboard && leaderboard.length > 0 && (
                        <div style={{
                            margin: "16px 0 20px",
                            display: "flex",
                            flexDirection: "column",
                            gap: "4px"
                        }}>
                            <p style={{ marginBottom: "10px", opacity: 0.5, fontSize: "11px", textTransform: "uppercase", letterSpacing: "0.08em" }}>
                                Final Standings
                            </p>
                            {leaderboard.map((p, index) => {
                                const medals = ["🥇", "🥈", "🥉"];
                                const medal = medals[index] ?? `#${index + 1}`;
                                const isMe = p.playerId === storedPlayer?.id;
                                return (
                                    <div key={p.playerId ?? index} style={{
                                        display: "flex",
                                        alignItems: "center",
                                        gap: "10px",
                                        padding: "6px 4px",
                                        borderBottom: "1px solid rgba(255,255,255,0.06)",
                                        fontWeight: isMe ? "bold" : "normal",
                                        opacity: isMe ? 1 : 0.75
                                    }}>
                    <span style={{ width: "28px", textAlign: "center", fontSize: "16px" }}>
                        {medal}
                    </span>
                                        <span style={{ flex: 1, color: isMe ? "#38bdf8" : "inherit" }}>
                        {p.username ?? `Player ${index + 1}`}
                                            {isMe && (
                                                <span style={{
                                                    fontSize: "10px",
                                                    color: "#38bdf8",
                                                    marginLeft: "6px",
                                                    opacity: 0.8
                                                }}>
                                (du)
                            </span>
                                            )}
                    </span>
                                        <span style={{ fontSize: "13px", opacity: 0.8 }}>
                        {(p.score ?? 0).toLocaleString("de-DE")} pts
                    </span>
                                    </div>
                                );
                            })}
                        </div>
                    )}

                    {/* Session Info */}
                    <p style={{ opacity: 0.6, fontSize: "12px", marginBottom: "4px" }}>
                        Session Code: <strong>{sessionCode}</strong> · Player ID: <strong>{storedPlayer?.id}</strong>
                    </p>
                    <p style={{ opacity: 0.5, fontSize: "11px", marginBottom: "16px" }}>
                        Save this info to resume later.
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
