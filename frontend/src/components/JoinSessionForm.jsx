import { useState } from "react";
import api from "../api/api";

function JoinSessionForm({ player, onSessionJoined }) {
    const [sessionCode, setSessionCode] = useState("");
    const [message, setMessage] = useState("");
    const [isError, setIsError] = useState(false);

    const handleJoinSession = async () => {
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

            localStorage.setItem("activePlayerId", player.id);
            onSessionJoined(response.data);
            setMessage(`Joined session: ${response.data.sessionCode}`);
            setIsError(false);
            setSessionCode("");
        } catch (error) {
            console.error(error);
            setMessage(error.response?.data?.message || "Failed to join session");
            setIsError(true);
        }
    };

    return (
        <div className="form-card-content">
            <h2>Join Session</h2>

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

            <button className="main-action-button" onClick={handleJoinSession}>
                Join Session
            </button>
        </div>
    );
}

export default JoinSessionForm;