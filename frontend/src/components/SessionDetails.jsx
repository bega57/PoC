import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

function SessionDetails({ session }) {
    const navigate = useNavigate();

    useEffect(() => {
        if (session && session.status === "RUNNING") {
            navigate(`/game/${session.sessionCode}`);
        }
    }, [session, navigate]);
    if (!session) {
        return (
            <div>
                <h2>Session Details</h2>
                <p>No session selected yet.</p>
            </div>
        );
    }

    return (
        <div>
            <h2>Session Details</h2>
            <p>Session Code: {session.sessionCode}</p>
            <p>Status: {session.status}</p>
            <p>Players: {session.players.length} / {session.maxPlayers}</p>
            <p>Max Players: {session.maxPlayers}</p>

            <p>
                Players: {session.players.length} / {session.maxPlayers}
            </p>

            {session.status === "WAITING" && (
                <p>
                    Waiting for {session.maxPlayers - session.players.length} more player(s)...
                </p>
            )}

            {session.status === "RUNNING" && (
                <p>Game is running.</p>
            )}

            <h3>Players</h3>
            {session.players.length === 0 ? (
                <p>No players in this session yet.</p>
            ) : (
                <ul>
                    {session.players.map((player) => (
                        <li key={player.id}>
                            {player.username} (ID: {player.id})
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default SessionDetails;