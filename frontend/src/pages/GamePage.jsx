import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";

function GamePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [session, setSession] = useState(null);
    const [selectedAction, setSelectedAction] = useState(null);

    useEffect(() => {
        const fetchData = () => {
            api.get(`/sessions/${sessionCode}`)
                .then((res) => setSession(res.data))
                .catch((err) => console.error(err));
        };

        fetchData();
        const interval = setInterval(fetchData, 2000);

        return () => clearInterval(interval);
    }, [sessionCode]);

    const ports = {
        London: { x: 520, y: 180 },
        "New York": { x: 220, y: 230 },
        "Cape Town": { x: 520, y: 430 },
    };

    if (!session) {
        return <div style={{ color: "white", padding: "20px" }}>Loading game...</div>;
    }

    return (
        <div style={{ display: "flex", height: "100vh", fontFamily: "Arial" }}>
            <div style={{ flex: 3, position: "relative", overflow: "hidden" }}>
                <img
                    src="/world-map.png"
                    alt="map"
                    style={{
                        width: "100%",
                        height: "100%",
                        objectFit: "cover",
                        filter: "brightness(0.9)",
                    }}
                />

                {Object.entries(ports).map(([name, pos]) => (
                    <div
                        key={name}
                        style={{
                            position: "absolute",
                            left: pos.x,
                            top: pos.y,
                            transform: "translate(-50%, -50%)",
                            cursor: "pointer",
                            fontSize: "20px",
                        }}
                        title={name}
                    >
                        ⚓
                    </div>
                ))}

                {session.players.map((p, i) => {
                    const port = ports["London"];

                    return (
                        <div
                            key={p.id}
                            style={{
                                position: "absolute",
                                left: port.x + i * 15,
                                top: port.y + i * 15,
                                transform: "translate(-50%, -50%)",
                                fontSize: "26px",
                                textAlign: "center",
                            }}
                        >
                            🚢
                            <div
                                style={{
                                    fontSize: "10px",
                                    color: "white",
                                    background: "rgba(0,0,0,0.6)",
                                    padding: "2px 4px",
                                    borderRadius: "4px",
                                }}
                            >
                                {p.username}
                            </div>
                        </div>
                    );
                })}
            </div>

            <div
                style={{
                    flex: 1,
                    background: "#1e1e1e",
                    color: "white",
                    padding: "20px",
                    display: "flex",
                    flexDirection: "column",
                    gap: "15px",
                    boxShadow: "-2px 0 10px rgba(0,0,0,0.5)",
                }}
            >
                <h2 style={{ marginBottom: "10px" }}>⚙️ Actions</h2>

                <button
                    onClick={() => navigate(`/market/${sessionCode}`)}
                    style={buttonStyle}
                >
                    🚢 Ship Market
                </button>

                <button
                    onClick={() => setSelectedAction("trade")}
                    style={buttonStyle}
                >
                    💰 Trade
                </button>

                <button
                    onClick={() => setSelectedAction("voyage")}
                    style={buttonStyle}
                >
                    🌍 Voyage
                </button>

                <div
                    style={{
                        marginTop: "20px",
                        background: "#2a2a2a",
                        padding: "15px",
                        borderRadius: "8px",
                        minHeight: "150px",
                    }}
                >
                    {selectedAction === "trade" && (
                        <div>
                            <h3>Trade</h3>
                            <p>Buy and sell goods (coming soon)</p>
                        </div>
                    )}

                    {selectedAction === "voyage" && (
                        <div>
                            <h3>Voyage</h3>
                            <p>Select a port to travel</p>
                        </div>
                    )}

                    {!selectedAction && <p>Select an action...</p>}
                </div>

                <div style={{ marginTop: "auto" }}>
                    <h3>Players</h3>
                    <ul>
                        {session.players.map((p) => (
                            <li key={p.id}>{p.username}</li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
}

const buttonStyle = {
    padding: "10px",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    background: "#3a3a3a",
    color: "white",
    transition: "0.2s",
};

export default GamePage;