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

const geoUrl = "/countries-110m.json";

function GamePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [session, setSession] = useState(null);
    const [selectedAction, setSelectedAction] = useState(null);
    const [sidebarOpen, setSidebarOpen] = useState(true);

    const [showWelcome, setShowWelcome] = useState(() => {
        return sessionStorage.getItem(`welcomeShown-${sessionCode}`) !== "true";
    });

    const storedPlayer = JSON.parse(localStorage.getItem("player"));

    const [selectedPort, setSelectedPort] = useState(null);
    const [confirmedPort, setConfirmedPort] = useState(null);

    useEffect(() => {
        const fetchData = () => {
            api.get(`/sessions/${sessionCode}`)
                .then((res) => {
                    setSession(res.data);

                    const me = res.data.players.find(p => p.id === storedPlayer.id);
                    if (me?.currentPort && confirmedPort !== me.currentPort) {
                        setConfirmedPort(me.currentPort);

                        localStorage.setItem("currentPort", me.currentPort);
                    }
                })
                .catch((err) => console.error(err));
        };

        fetchData();
        const interval = setInterval(fetchData, 2000);

        return () => clearInterval(interval);
    }, [sessionCode]);

    useEffect(() => {
        const timer = setTimeout(() => {
            setShowWelcome(false);
            sessionStorage.setItem(`welcomeShown-${sessionCode}`, "true");
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
        { name: "Buenos Aires", coordinates: [-58.3816, -34.6037] },
        { name: "Lima", coordinates: [-77.0428, -12.0464] },
        { name: "Vancouver", coordinates: [-123.1207, 49.2827] },
        { name: "Tokyo", coordinates: [139.6917, 35.6895] },
        { name: "Shanghai", coordinates: [121.4737, 31.2304] },
        { name: "Bangkok", coordinates: [100.5018, 13.7563] },
        { name: "Jakarta", coordinates: [106.8456, -6.2088] },
        { name: "Istanbul", coordinates: [28.9784, 41.0082] },
        { name: "Sydney", coordinates: [151.2093, -33.8688] },
        { name: "Dubai", coordinates: [55.2708, 25.2048] },
        { name: "Singapore", coordinates: [103.8198, 1.3521] },
        { name: "Mumbai", coordinates: [72.8777, 19.076] },
        { name: "Cape Town", coordinates: [18.4241, -33.9249] },
        { name: "Lagos", coordinates: [3.3792, 6.5244] },
        { name: "Mombasa", coordinates: [39.6682, -4.0435] },
        { name: "Rio", coordinates: [-43.1729, -22.9068] },
        { name: "Los Angeles", coordinates: [-118.2437, 34.0522] },
        { name: "Hamburg", coordinates: [9.9937, 53.5511 + 1] },
        { name: "Rotterdam", coordinates: [4.47917, 51.9225 - 1] },
        { name: "Seoul", coordinates: [126.978, 37.5665] },
        { name: "Honolulu", coordinates: [-157.8583, 21.3069] }
    ];

    if (!session) {
        return <div style={{ color: "white", padding: "20px" }}>Loading game...</div>;
    }

    return (
        <div className="game-container">

            {/* MAP */}
            <div className="map-container">
                <ComposableMap
                    projection="geoEqualEarth"
                    projectionConfig={{
                        scale: 220
                    }}
                    style={{ width: "100%", height: "100%" }}
                >
                    <Geographies geography={geoUrl}>
                        {({ geographies }) =>
                            geographies.map((geo) => (
                                <Geography
                                    key={geo.rsmKey}
                                    geography={geo}
                                    fill="#243447"
                                    stroke="#1b2838"
                                    style={{
                                        default: { fill: "#243447", outline: "none" },
                                        hover: { fill: "#243447", outline: "none" },
                                        pressed: { outline: "none" }
                                    }}
                                />
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
                            <circle
                                r={
                                    port.name === confirmedPort
                                        ? 10
                                        : port.name === selectedPort
                                            ? 8
                                            : 5
                                }
                                fill={
                                    port.name === confirmedPort
                                        ? "lime"
                                        : port.name === selectedPort
                                            ? "orange"
                                            : "red"
                                }
                                style={{
                                    cursor: "pointer",
                                    transition: "0.2s"
                                }}
                            />
                            <text
                                y={-10}
                                dx={5}
                                style={{
                                    fontSize: "10px",
                                    fill: "white",
                                    pointerEvents: "none"
                                }}
                            >
                                {port.name}
                            </text>
                        </Marker>
                    ))}

                    {session.players
                        .filter(p => p.status === "ACTIVE" && p.currentPort)
                        .map((p) => {
                            const port = ports.find(pt => pt.name === p.currentPort);
                            if (!port) return null;

                            return (
                                <Marker key={p.id} coordinates={port.coordinates}>
                                    <text
                                        y={20}
                                        style={{
                                            fill: "white",
                                            fontSize: "12px",
                                            fontWeight: "600",
                                            textShadow: "0 0 6px rgba(0,0,0,0.9)"
                                        }}
                                    >
                                        {p.username}
                                        {p.currentPort && p.ships?.length > 0 && " 🚢"}
                                    </text>
                                </Marker>
                            );
                        })}
                </ComposableMap>
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
                            onClick={() => navigate(`/voyage/${sessionCode}`)}
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
                            sessionStorage.setItem(`welcomeShown-${sessionCode}`, "true");
                        }}>
                            Start Playing
                        </button>

                    </div>
                </div>

            )}

            {selectedPort && !confirmedPort && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>Select {selectedPort} as your main port?</h2>

                        <button className="confirm-btn"
                                onClick={async () => {
                                    const res = await api.post(`/players/select-port`, {
                                        playerId: storedPlayer.id,
                                        port: selectedPort
                                    });

                                    setConfirmedPort(res.data.currentPort);

                                    localStorage.setItem("currentPort", res.data.currentPort);
                                    setSelectedPort(null);
                                }}
                        >
                            Confirm
                        </button>

                        <button className="cancel-btn" onClick={() => setSelectedPort(null)}>
                            Cancel
                        </button>
                    </div>
                </div>
            )}

        </div>
    );
}

export default GamePage;