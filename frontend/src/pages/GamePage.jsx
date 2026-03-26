import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import "./GamePage.css";

import {
    ComposableMap,
    Geographies,
    Geography,
    Marker
} from "react-simple-maps";

const geoUrl =
    "https://raw.githubusercontent.com/deldersveld/topojson/master/world-countries.json";

function GamePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [session, setSession] = useState(null);
    const [selectedAction, setSelectedAction] = useState(null);
    const [sidebarOpen, setSidebarOpen] = useState(true);

    const [showWelcome, setShowWelcome] = useState(() => {
        return sessionStorage.getItem("welcomeShown") !== "true";
    });
    const storedPlayer = JSON.parse(localStorage.getItem("player"));

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

    useEffect(() => {
        const timer = setTimeout(() => {
            setShowWelcome(false);
            sessionStorage.setItem("welcomeShown", "true");
        }, 60000);

        return () => clearTimeout(timer);
    }, []);

    const handleLeaveSession = async () => {
        if (!storedPlayer?.id) return;

        try {
            await api.post(`/sessions/${sessionCode}/leave`, {
                playerId: storedPlayer.id,
            });

            navigate("/");
        } catch (error) {
            console.error("Failed to leave session:", error);
        }
    };

    const ports = [
        { name: "London", coordinates: [-0.1276, 51.5072] },
        { name: "New York", coordinates: [-74.006, 40.7128] },
        { name: "Tokyo", coordinates: [139.6917, 35.6895] },
    ];

    if (!session) {
        return <div style={{ color: "white", padding: "20px" }}>Loading game...</div>;
    }

    return (
        <div className="game-container">

            {/* MAP */}
            <div className="map-container">
                <ComposableMap>
                    <Geographies geography={geoUrl}>
                        {({ geographies }) =>
                            geographies.map((geo) => (
                                <Geography key={geo.rsmKey} geography={geo} />
                            ))
                        }
                    </Geographies>

                    {ports.map((port) => (
                        <Marker
                            key={port.name}
                            coordinates={port.coordinates}
                            onClick={() => {
                                if (!confirmedPort && !showWelcome) {
                                    setSelectedPort(port.name);
                                }
                            }}
                        >
                            <circle r={5} fill="red" />
                            <text y={-10} style={{ fontSize: "10px", fill: "white" }}>
                                {port.name}
                            </text>
                        </Marker>
                    ))}
                </ComposableMap>

                {Object.entries(ports).map(([name, pos]) => (
                    <div
                        key={name}
                        className="port"
                        style={{ left: pos.x, top: pos.y }}
                        title={name}
                    >
                        ⚓
                    </div>
                ))}

                {session.players
                    .filter(p => p.status === "ACTIVE" && p.currentPort)
                    .map((p, i) => {
                        const port = ports.find(pt => pt.name === p.currentPort);
                        if (!port) return null;

                        return (
                            <Marker
                                key={p.id}
                                coordinates={port.coordinates}
                            >
                                <text y={20} style={{ fill: "yellow", fontSize: "12px" }}>
                                    {p.username} {p.ships.length > 0 && "🚢"}
                                </text>
                            </Marker>
                        );
                    })}
            </div>

            <div className={`sidebar ${sidebarOpen ? "open" : "closed"}`}>

                <button
                    className="toggle-btn"
                    onClick={() => setSidebarOpen(!sidebarOpen)}
                >
                    {sidebarOpen ? "❯" : "❮"}
                </button>

                {sidebarOpen && (
                    <>
                        <h2 className="sidebar-title">Actions</h2>

                        <button
                            className="action-btn"
                            onClick={() => navigate(`/market/${sessionCode}`)}
                        >
                            Ship Market
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => navigate(`/company/${sessionCode}`)}
                        >
                            Company
                        </button>

                        <button
                            className="action-btn"
                            onClick={() => setSelectedAction("voyage")}
                        >
                            Voyage
                        </button>

                        <div className="players-section">
                            <h3>Players</h3>
                            <div className="player-list">
                                {session.players.map((p) => (
                                    <div key={p.id} className="player-item">
                                        {p.username} {p.status === "DISCONNECTED" ? "(disconnected)" : "(active)"}
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="leave-section">
                            <button
                                className="action-btn leave-btn"
                                onClick={handleLeaveSession}
                            >
                                Leave Session
                            </button>
                        </div>
                    </>
                )}
            </div>

            {showWelcome && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>⚓ Welcome aboard, {storedPlayer?.username}!</h2>
                        <p>You start with:</p>
                        <h1>$5000</h1>

                        <button onClick={() => {
                            setShowWelcome(false);
                            sessionStorage.setItem("welcomeShown", "true");
                        }}>
                            Start Playing
                        </button>

                    </div>
                </div>

            )}

        </div>
    );
}

export default GamePage;