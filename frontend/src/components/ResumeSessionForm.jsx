import { useState } from "react";
import api from "../api/api";

function ResumeSessionForm({ onSessionResumed, onPlayerResumed }) {
    const [sessionCode, setSessionCode] = useState("");
    const [playerId, setPlayerId] = useState("");
    const [message, setMessage] = useState("");
    const [isError, setIsError] = useState(false);

    const handleResumeSession = async (e) => {
        e.preventDefault();
        if (!sessionCode.trim()) {
            setMessage("Session code is required.");
            setIsError(true);
            return;
        }

        if (!playerId.trim()) {
            setMessage("Player ID is required.");
            setIsError(true);
            return;
        }

        try {
            const response = await api.post(`/sessions/${sessionCode}/resume`, {
                playerId: Number(playerId),
            });

            const playerResponse = await api.get(`/players/${playerId}`);
            const loadedPlayer = playerResponse.data;

            sessionStorage.setItem(`activePlayerId-${response.data.sessionCode}`, String(playerId));
            sessionStorage.setItem(`player-${response.data.sessionCode}`, JSON.stringify(loadedPlayer));

            onPlayerResumed(loadedPlayer);
            onSessionResumed(response.data);

            setMessage(`Resumed session: ${response.data.sessionCode}`);
            setIsError(false);
            setSessionCode("");
            setPlayerId("");
        } catch (error) {
            console.error(error);
            setMessage(error.response?.data?.message || "Failed to resume session");
            setIsError(true);
        }
    };

    return (
        <div className="form-card-content">
            <h2>Resume Session</h2>

            <form onSubmit={handleResumeSession}>
                <div className="resume-input-row">
                    <input
                        type="text"
                        placeholder="Session code"
                        value={sessionCode}
                        onChange={(e) => setSessionCode(e.target.value)}
                    />

                    <input
                        type="number"
                        placeholder="Player ID"
                        value={playerId}
                        onChange={(e) => setPlayerId(e.target.value)}
                    />
                </div>

                {message && (
                    <p
                        className="form-message"
                        style={{
                            color: isError ? "#ff6b6b" : "#4ade80",
                        }}
                    >
                        {message}
                    </p>
                )}

                <button type="submit" className="main-action-button">
                    Resume Session
                </button>
            </form>
        </div>
    );
}

export default ResumeSessionForm;