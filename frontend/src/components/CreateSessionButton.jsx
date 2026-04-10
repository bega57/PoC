import { useState } from "react";
import api from "../api/api";

function CreateSessionButton({ player, onSessionCreated }) {
    const [maxPlayers, setMaxPlayers] = useState(5);
    const [message, setMessage] = useState("");

    const handleCreateSession = async () => {
        if (!player) {
            setMessage("Player is required.");
            return;
        }

        try {
            const response = await api.post("/sessions", {
                playerId: player.id,
                maxPlayers: maxPlayers,
            });

            sessionStorage.setItem(`activePlayerId-${response.data.sessionCode}`, player.id);
            sessionStorage.setItem(`player-${response.data.sessionCode}`, JSON.stringify(player));

            onSessionCreated(response.data);
            setMessage("");
        } catch (error) {
            console.error(error);
            setMessage(error.response?.data?.message || "Failed to create session");
        }
    };

    return (
        <div className="form-card-content">
            <h2>Create Session</h2>

            <div className="top-section">
                <p className="field-label">Choose player count:</p>

                <div className="player-count-selector five-options">
                    {[1, 2, 3, 4, 5].map((count) => (
                        <button
                            key={count}
                            type="button"
                            className={`count-button ${maxPlayers === count ? "active" : ""}`}
                            onClick={() => setMaxPlayers(count)}
                        >
                            {count}
                        </button>
                    ))}
                </div>

                {message && (
                    <p className="form-message" style={{ color: "#ff6b6b" }}>
                        {message}
                    </p>
                )}
            </div>

            <button className="main-action-button" onClick={handleCreateSession}>
                Create Session
            </button>
        </div>
    );
}

export default CreateSessionButton;