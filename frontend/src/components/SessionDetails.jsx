import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

function SessionDetails({ session, player }) {
    const navigate = useNavigate();

    useEffect(() => {
        if (session && session.status === "RUNNING") {
            navigate(`/game/${session.sessionCode}`);
        }
    }, [session, navigate]);

    return (
        <div style={{ width: "100%" }}>
            <h2 style={{ textAlign: "left", marginTop: 0, marginBottom: "16px" }}>
                Details
            </h2>

            <p>
                Player: {player ? `${player.username} (ID: ${player.id})` : "No player created yet"}
            </p>

            {session && (
                <>
                    <p>Session Code: {session.sessionCode}</p>
                    <p>Status: {session.status}</p>
                    <p>Players: {session.players.length} / {session.maxPlayers}</p>

                    {session.status === "WAITING" && (
                        <p>
                            Waiting for {session.maxPlayers - session.players.length} more player(s)...
                        </p>
                    )}

                    {session.status === "RUNNING" && (
                        <p>Game is running.</p>
                    )}

                    <h3 style={{ textAlign: "left" }}>Players</h3>
                    {session.players.length === 0 ? (
                        <p>No players in this session yet.</p>
                    ) : (
                        <ul>
                            {session.players.map((sessionPlayer) => (
                                <li key={sessionPlayer.id}>
                                    {sessionPlayer.username} (ID: {sessionPlayer.id})
                                </li>
                            ))}
                        </ul>
                    )}
                </>
            )}
        </div>
    );
}

export default SessionDetails;