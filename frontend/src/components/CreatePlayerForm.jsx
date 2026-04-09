import { useState } from "react";
import api from "../api/api";

function CreatePlayerForm({ onPlayerCreated }) {
    const [username, setUsername] = useState("");
    const [message, setMessage] = useState("");

    const handleCreatePlayer = async () => {
        if (!username.trim()) {
            setMessage("Enter a username");
            return;
        }

        try {
            const response = await api.post("/players", { username });
            onPlayerCreated(response.data);
            localStorage.setItem("player-global", JSON.stringify(response.data));
            localStorage.setItem("activePlayerId-global", response.data.id);
            setMessage(`Player created: ${response.data.username} (ID: ${response.data.id})`);
            setUsername("");
        } catch (error) {
            console.error(error);
            setMessage(error.response?.data?.message || "Failed to create player");
        }
    };

    return (
        <div className="form-card-content">
            <h2>Create Player</h2>

            <div className="top-section">
                <input
                    type="text"
                    placeholder="Enter username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>

            <button className="main-action-button" onClick={handleCreatePlayer}>
                Create Player
            </button>
        </div>
    );
}

export default CreatePlayerForm;