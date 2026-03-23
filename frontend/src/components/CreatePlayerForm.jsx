import { useState } from "react";
import api from "../api/api";

function CreatePlayerForm({ onPlayerCreated }) {
    const [username, setUsername] = useState("");
    const [message, setMessage] = useState("");

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            const response = await api.post("/players", { username });
            onPlayerCreated(response.data);
            setMessage(`Player created: ${response.data.username} (ID: ${response.data.id})`);
            setUsername("");
        } catch (error) {
            setMessage(error.response?.data?.message || "Failed to create player");
        }
    };

    return (
        <div>
            <h2>Create Player</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Enter username"
                    value={username}
                    onChange={(event) => setUsername(event.target.value)}
                />
                <button type="submit">Create Player</button>
            </form>
            <p>{message}</p>
        </div>
    );
}

export default CreatePlayerForm;