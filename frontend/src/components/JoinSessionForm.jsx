import { useState } from "react";
import api from "../api/api";

function JoinSessionForm({ player, onSessionJoined, showToast }) {
    const [sessionCode, setSessionCode] = useState("");
    const [message, setMessage] = useState("");
    const [isError, setIsError] = useState(false);

    const handleJoinSession = async (e) => {
        e.preventDefault();
        if (!player) {
            setMessage("Player is required.");
            setIsError(true);
            return;
        }

        if (!sessionCode.trim()) {
            setMessage("Session code is required.");
            setIsError(true);
            return;
        }

        try {
            const response = await api.post(`/sessions/${sessionCode}/join`, {
                playerId: player.id,
            });

            sessionStorage.setItem(`activePlayerId-${response.data.sessionCode}`, String(player.id));
            sessionStorage.setItem(`player-${response.data.sessionCode}`, JSON.stringify(player));

            onSessionJoined(response.data);
            setMessage(`Joined session: ${response.data.sessionCode}`);
            setIsError(false);
            setSessionCode("");
            showToast(`Joined session: ${response.data.sessionCode}`, "success");
        } catch (error) {
            console.error(error);
            setMessage(error.response?.data?.message || "Failed to join session");
            setIsError(true);
            showToast(error.response?.data?.message || "Failed to join session", "error");
        }
    };

    return (
        <div className="form-card-content">
            <h2>Join Session</h2>

            <form onSubmit={handleJoinSession}>
                <input
                    type="text"
                    placeholder="Enter session code"
                    value={sessionCode}
                    onChange={(e) => setSessionCode(e.target.value)}
                />

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
                    Join Session
                </button>
            </form>
        </div>
    );
}

export default JoinSessionForm;