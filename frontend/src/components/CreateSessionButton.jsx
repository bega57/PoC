import { useState } from "react";
import api from "../api/api";

function CreateSessionButton({ player, onSessionCreated, showToast }) {
    const [maxPlayers, setMaxPlayers] = useState(1);
    const [message, setMessage] = useState("");

    const handleCreateSession = async (e) => {
        e.preventDefault();
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
            showToast(`${maxPlayers} personen session created`, "success");
        } catch (error) {
            console.error(error);
            setMessage(error.response?.data?.message || "Failed to create session");
            showToast(error.response?.data?.message || "Failed to create session", "error");
        }
    };

    const handleFormKeyDown = async (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            await handleCreateSession(e);
        }
    };

    return (
        <div className="form-card-content">
            <h2>Create Session</h2>

            <form onSubmit={handleCreateSession} className="form create-session-form">

                <div className="player-count-selector">
                    {[1,2,3,4,5].map(count => (
                        <button
                            key={count}
                            type="button"
                            className={maxPlayers === count ? "active" : ""}
                            onClick={() => setMaxPlayers(count)}
                        >
                            {count}
                        </button>
                    ))}
                </div>

                <p className="form-message">{message || ""}</p>

                <button type="submit" className="main-action-button">
                    Create Session
                </button>
            </form>
        </div>
    );
}

export default CreateSessionButton;