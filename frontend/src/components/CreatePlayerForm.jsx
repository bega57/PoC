import { useState } from "react";
import api from "../api/api";

function CreatePlayerForm({ onPlayerCreated, showToast }) {
    const [username, setUsername] = useState("");
    const [message, setMessage] = useState("");

    const handleCreatePlayer = async (e) => {
        e.preventDefault();
        if (!username.trim()) {
            setMessage("Enter a username");
            return;
        }

        try {
            const response = await api.post("/players", { username });
            onPlayerCreated(response.data);
            sessionStorage.setItem("player-global", JSON.stringify(response.data));
            sessionStorage.setItem("activePlayerId-global", response.data.id);
            setMessage(`Player created: ${response.data.username} (ID: ${response.data.id})`);
            setUsername("");
            showToast(`Player created: ${response.data.username}`, "success");
        } catch (error) {
            console.error(error);
            setMessage(error.response?.data?.message || "Failed to create player");
            showToast(error.response?.data?.message || "Failed to create player", "error");
        }
    };

    return (
        <div className="form-card-content">
            <h2>Create Player</h2>

            <form onSubmit={handleCreatePlayer}>
                <div className="top-section">
                    <input
                        type="text"
                        placeholder="Enter username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>

                <button type="submit" className="main-action-button">
                    Create Player
                </button>
            </form>
        </div>
    );
}

export default CreatePlayerForm;