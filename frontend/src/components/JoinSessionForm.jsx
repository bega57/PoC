import { useState } from "react";
import api from "../api/api";

function JoinSessionForm({ player, onSessionJoined }) {
    const [sessionCode, setSessionCode] = useState("");
    const [message, setMessage] = useState("");

    const handleJoinSession = async (event) => {
        event.preventDefault();

        if (!player) {
            setMessage("Create a player first");
            return;
        }

        try {
            const response = await api.post(`/sessions/${sessionCode}/join`, {
                playerId: player.id,
            });

            onSessionJoined(response.data);
            setMessage(`Joined session: ${response.data.sessionCode}`);
            setSessionCode("");
        } catch (error) {
            setMessage(error.response?.data?.message || "Failed to join session");
        }
    };

    return (
        <div>
            <h2>Join Session</h2>
            <form onSubmit={handleJoinSession}>
                <input
                    type="text"
                    placeholder="Enter session code"
                    value={sessionCode}
                    onChange={(event) => setSessionCode(event.target.value)}
                />
                <button type="submit">Join Session</button>
            </form>
            <p>{message}</p>
        </div>
    );
}

export default JoinSessionForm;