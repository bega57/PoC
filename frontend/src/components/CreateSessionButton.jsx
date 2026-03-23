import { useState } from "react";
import api from "../api/api";

function CreateSessionButton({ player, onSessionCreated }) {
    const [maxPlayers, setMaxPlayers] = useState(5);
    const [message, setMessage] = useState("");

    const handleCreateSession = async () => {
        if (!player) {
            setMessage("Create a player first");
            return;
        }

        try {
            const response = await api.post("/sessions", {
                playerId: player.id,
                maxPlayers: maxPlayers,
            });

            onSessionCreated(response.data);

            if (maxPlayers === 1) {
                setMessage(`Session created and started: ${response.data.sessionCode}`);
            } else {
                setMessage(`Session created: ${response.data.sessionCode}`);
            }
        } catch (error) {
            console.error(error);
            setMessage(error.response?.data?.message || "Failed to create session");
        }
    };

    return (
        <div>
            <h2>Create Session</h2>

            <p>Max Players:</p>
            <select
                value={maxPlayers}
                onChange={(e) => setMaxPlayers(Number(e.target.value))}
            >
                <option value={1}>1</option>
                <option value={2}>2</option>
                <option value={3}>3</option>
                <option value={4}>4</option>
                <option value={5}>5</option>
            </select>

            <br /><br />

            <button onClick={handleCreateSession}>Create Session</button>

            <p>{message}</p>
        </div>
    );
}

export default CreateSessionButton;