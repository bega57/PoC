import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import "./GamePage.css";
import "leaflet/dist/leaflet.css";
import cheapShip from "../assets/ships/cheapSide.png";
import middleShip from "../assets/ships/middleSide.png";
import expensiveShip from "../assets/ships/expensiveSide.png";

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
    const [sidebarOpen, setSidebarOpen] = useState(true);

    const [showWelcome, setShowWelcome] = useState(() => {
        return sessionStorage.getItem(`welcomeShown-${sessionCode}`) !== "true";
    });

    const storedPlayer = JSON.parse(localStorage.getItem("player"));
    const currentPlayer = session?.players?.find(p => p.id === storedPlayer?.id) || null;

    const [selectedPort, setSelectedPort] = useState(null);

    const [showPortInstruction, setShowPortInstruction] = useState(false);

    const [voyages, setVoyages] = useState([]);

    const [selectedShip, setSelectedShip] = useState(null);

    const [ports, setPorts] = useState([]);

    const [hoveredPort, setHoveredPort] = useState(null);
    const [portCargo, setPortCargo] = useState([]);
    const [cargoCache, setCargoCache] = useState({});

    const savedPort = localStorage.getItem(`currentPort-${sessionCode}`);

    const [showRewardPopup, setShowRewardPopup] = useState(false);

    const [rewardAmount, setRewardAmount] = useState(0);

    const [lastFinishedVoyageId, setLastFinishedVoyageId] = useState(() => {
        const saved = sessionStorage.getItem(`lastFinishedVoyageId-${sessionCode}`);
        return saved ? Number(saved) : null;
    });

    const currentPort =
        selectedShip?.currentPort ||
        savedPort ||
        null;

    useEffect(() => {
        if (!session || !storedPlayer) return;

        const myShips = session.players.find(p => p.id === storedPlayer.id)?.ships || [];
        const myShipIds = myShips.map(s => s.id);

        const newestFinishedVoyage = [...voyages]
            .filter(v =>
                myShipIds.includes(v.shipId) &&
                v.status === "FINISHED"
            )
            .sort((a, b) => b.id - a.id)[0];

        if (!newestFinishedVoyage) return;

        if (newestFinishedVoyage.id === lastFinishedVoyageId) return;

        console.log("FINISHED VOYAGE FOUND:", newestFinishedVoyage);

        setRewardAmount(newestFinishedVoyage.reward || 0);
        setLastFinishedVoyageId(newestFinishedVoyage.id);
        sessionStorage.setItem(
            `lastFinishedVoyageId-${sessionCode}`,
            String(newestFinishedVoyage.id)
        );
        setShowRewardPopup(true);
    }, [voyages, session, sessionCode, storedPlayer, lastFinishedVoyageId]);

    useEffect(() => {
        if (!session || !storedPlayer) return;

        const me = session.players.find(p => p.id === storedPlayer.id);
        if (!me?.ships?.length) return;

        const backendShip = me.ships[0];
        const savedPort = localStorage.getItem(`currentPort-${sessionCode}`)

        setSelectedShip(prev => {
            if (prev?.id === backendShip.id) {
                return {
                    ...prev,
                    currentPort: backendShip.currentPort || prev.currentPort || savedPort
                };
            }

            return {
                ...backendShip,
                currentPort: backendShip.currentPort || savedPort
            };
        });
    }, [session]);


    useEffect(() => {
        const fetchData = () => {
            api.get(`/sessions/${sessionCode}`)
                .then((res) => {
                    setSession(res.data);

                })
                .catch((err) => console.error(err));

            api.get("/voyages")
                .then(res => setVoyages(res.data))
                .catch(err => console.error(err));
        };

        fetchData();
        const interval = setInterval(() => {
            fetchData();
        }, 2000);

        return () => clearInterval(interval);
    }, [sessionCode]);


    useEffect(() => {
        const timer = setTimeout(() => {
            setShowWelcome(false);
            sessionStorage.setItem(`welcomeShown-${sessionCode}`, "true");
        }, 60000);

        return () => clearTimeout(timer);
    }, []);

    useEffect(() => {
        api.get("/ports")
            .then(res => setPorts(res.data))
            .catch(err => console.error(err));
    }, []);

    const handlePortHover = async (port) => {
        setHoveredPort(port);

        const portName = port.name;

        if (cargoCache[portName]) {
            setPortCargo(cargoCache[portName]);
            return;
        }

        try {
            const res = await api.get(`/cargo?portName=${portName}`);
            setPortCargo(res.data);

            setCargoCache(prev => ({
                ...prev,
                [portName]: res.data
            }));
        } catch (err) {
            console.error(err);
            setPortCargo([]);
        }
    };

    useEffect(() => {
        const savedPort = localStorage.getItem(`currentPort-${sessionCode}`)

        if (savedPort) {
            setShowPortInstruction(false);
        }
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


    const getShipImage = (ship) => {
        switch (ship.type) {
            case "CHEAP":
                return cheapShip;
            case "MIDDLE":
                return middleShip;
            case "EXPENSIVE":
                return expensiveShip;
            default:
                return cheapShip;
        }
    };

    if (!session) {
        return <div style={{ color: "white", padding: "20px" }}>Loading game...</div>;
    }

    const myShips = session.players
        .find(p => p.id === storedPlayer.id)?.ships || [];


    const myShipIds = myShips.map(s => s.id);

    const myActiveVoyage = voyages.find(
        v => myShipIds.includes(v.shipId) && v.status === "RUNNING"
    );

    const rewards = portCargo.map(c => c.reward);
    const minReward = rewards.length > 0 ? Math.min(...rewards) : 0;
    const maxReward = rewards.length > 0 ? Math.max(...rewards) : 0;

    const riskLevels = portCargo.map(c => c.riskLevel);

    const riskOrder = ["LOW", "MEDIUM", "HIGH"];

    const minRisk = riskLevels.length > 0
        ? riskOrder[Math.min(...riskLevels.map(r => riskOrder.indexOf(r)))]
        : "LOW";

    const maxRisk = riskLevels.length > 0
        ? riskOrder[Math.max(...riskLevels.map(r => riskOrder.indexOf(r)))]
        : "LOW";

    const riskColor =
        maxRisk === "HIGH" ? "#ef4444" :
            maxRisk === "MEDIUM" ? "#f59e0b" :
                "#22c55e";

    const boxWidth = portCargo.length > 1 ? 150 : 130;

    const offsetX = 15;
    const offsetY = hoveredPort?.latitude > 0 ? 20 : -100;

    return (
        <div className="game-container">

            {/* MAP */}
            <div className="map-container">
                <ComposableMap
                    key={sessionCode}
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
                            coordinates={[port.longitude, port.latitude]}
                            onMouseEnter={() => handlePortHover(port)}
                            onMouseLeave={() => {
                                setTimeout(() => setHoveredPort(null), 80);
                            }}
                            onClick={() => {
                                if (!showWelcome && !showPortInstruction) {
                                    setSelectedPort(port.name);
                                }
                            }}
                        >

                            <>
                                {/* glow */}
                                {port.name === currentPort && (
                                    <circle
                                        r={12}
                                        fill="rgba(34,211,238,0.2)" // cyan glow
                                    />
                                )}

                                {/* punkt */}
                                <circle
                                    r={port.name === currentPort ? 6 : 5}
                                    fill={
                                        port.name === currentPort
                                            ? "#22d3ee" // current port
                                            : voyages.some(
                                                v =>
                                                    myShipIds.includes(v.shipId) &&
                                                    v.destinationPort === port.name &&
                                                    v.status === "FINISHED"
                                            )
                                                ? "#22c55e"
                                                : "#ef4444"
                                    }
                                />
                            </>

                            <text
                                x={port.name === "London" ? -8 : 10}
                                y={3}
                                textAnchor={port.name === "London" ? "end" : "start"}
                                style={{
                                    fontSize: "11px",
                                    fill: "#e2e8f0",
                                    fontWeight: "600",
                                    letterSpacing: "0.3px"
                                }}
                            >
                                {port.name}
                            </text>
                        </Marker>
                    ))}

                    {hoveredPort && (
                        <Marker
                            coordinates={[hoveredPort.longitude, hoveredPort.latitude]}
                        >
                            <g transform={`translate(${offsetX}, ${offsetY})`}>
                                <rect
                                    width={boxWidth}
                                    height={90}
                                    fill="#0f172a"
                                    stroke="#475569"
                                    rx={10}
                                />

                                <text x={10} y={18} fill="#e2e8f0" fontSize="11" fontWeight="600">
                                    Port Info
                                </text>

                                <line x1={10} y1={24} x2={boxWidth - 10} y2={24} stroke="#334155" />

                                <text x={10} y={40} fill="#cbd5f5" fontSize="10">
                                    📦 {portCargo.length} cargos
                                </text>

                                <text x={10} y={55} fill="#cbd5f5" fontSize="10">
                                    💰 {minReward === maxReward ? minReward : `${minReward} | ${maxReward}`}
                                </text>

                                <text x={10} y={70} fill={riskColor} fontSize="10">
                                    ⚠️ {minRisk === maxRisk ? minRisk : `${minRisk} | ${maxRisk}`}
                                </text>
                            </g>
                        </Marker>
                    )}

                    {session.players.flatMap(p =>
                        (p.ships || []).map(ship => ({ ship, player: p }))
                        ).map(({ ship, player }) => {
                                const voyage = voyages.find(
                                    v => v.shipId === ship.id && v.status !== "FINISHED"
                                );

                                // 🟢 FALL 1: ship ist im port
                                if (!voyage && ship.currentPort) {
                                    const port = ports.find(pt => pt.name === ship.currentPort);
                                    if (!port) return null;

                                    return (
                                        <Marker key={ship.id} coordinates={[port.longitude, port.latitude]}>
                                            <>
                                                <image
                                                    href={getShipImage(ship)}
                                                    width={36}
                                                    height={36}
                                                    x={-18}
                                                    y={-18}
                                                />

                                                {player.ships?.length > 0 && (
                                                    <text
                                                        y={16}
                                                        textAnchor="middle"
                                                        style={{
                                                            fill: "#cfe8ff",
                                                            fontSize: "11px",
                                                            fontWeight: "600",
                                                            textShadow: "0 0 4px rgba(0,0,0,0.8)"
                                                        }}
                                                    >
                                                        {player.username}
                                                    </text>
                                                )}
                                            </>
                                        </Marker>
                                    );
                                }

                                // 🔵 FALL 2: ship ist unterwegs → zwischen ports anzeigen
                                if (voyage) {
                                    const origin = ports.find(p => p.name === voyage.originPort);
                                    const dest = ports.find(p => p.name === voyage.destinationPort);
                                    if (!origin || !dest) return null;

                                    const start = new Date(voyage.startTime).getTime();
                                    const now = Date.now();

                                    let progress = 0;

                                    if (!voyage.arrivalTime) {
                                        progress = Math.min((now - start) / 20000, 1); // 20 sek travel fake
                                    } else {
                                        const end = new Date(voyage.arrivalTime).getTime();
                                        progress = Math.min((now - start) / (end - start), 1);
                                    }

                                    const lat =
                                        origin.latitude +
                                        (dest.latitude - origin.latitude) * progress;

                                    const lon =
                                        origin.longitude +
                                        (dest.longitude - origin.longitude) * progress;

                                    return (
                                        <Marker key={ship.id} coordinates={[lon, lat]}>
                                            <>
                                                <image
                                                    href={getShipImage(ship)}
                                                    width={24}
                                                    height={24}
                                                    x={-12}
                                                    y={-12}
                                                />

                                                {player.ships?.length > 0 && (
                                                    <text
                                                        y={16}
                                                        textAnchor="middle"
                                                        style={{
                                                            fill: "#cfe8ff",
                                                            fontSize: "10px",
                                                            fontWeight: "600",
                                                            textShadow: "0 0 4px rgba(0,0,0,0.8)"
                                                        }}
                                                    >
                                                        {player.username}
                                                    </text>
                                                )}
                                            </>
                                        </Marker>
                                    );
                                }

                                return null;
                            })}
                </ComposableMap>
            </div>

            <div className="status-bar">
                <p>
                    🕒 Game Day: {session.currentTick} |
                    🚢 Ship: {selectedShip?.name || "None"} |
                    📍 {currentPlayer?.currentPort || "No Port"} |
                    💰 Balance: {currentPlayer?.balance ?? "?"}
                </p>

                {myActiveVoyage && (
                    <p>
                        ⏳ Traveling {myActiveVoyage.originPort} → {myActiveVoyage.destinationPort}
                    </p>
                )}
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
                        <h2>⚓ Welcome aboard, {currentPlayer?.username || storedPlayer?.username}!</h2>
                        <p>You start with:</p>
                        <h1>40.000 Coins</h1>

                        <button onClick={() => {
                            setShowWelcome(false);
                            setShowPortInstruction(true);
                            sessionStorage.setItem(`welcomeShown-${sessionCode}`, "true");
                        }}>
                            Start Playing
                        </button>

                    </div>
                </div>

            )}

            {selectedPort && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>Select {selectedPort} as your main port?</h2>

                        <button className="confirm-btn"
                                onClick={async () => {
                                    const res = await api.post(`/players/select-port`, {
                                        playerId: storedPlayer.id,
                                        port: selectedPort
                                    });

                                    setSelectedShip(prev => ({
                                        ...(prev || {}),
                                        currentPort: res.data.currentPort
                                    }));

                                    localStorage.setItem(`currentPort-${sessionCode}`, res.data.currentPort);
                                    if (currentPlayer) {
                                        localStorage.setItem("player", JSON.stringify({
                                            ...currentPlayer,
                                            currentPort: res.data.currentPort
                                        }));
                                    }
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

            {showPortInstruction && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>🌍 Choose your main port</h2>
                        <p>
                            Click on any red port on the map to select your starting location.
                        </p>

                        <button onClick={() => setShowPortInstruction(false)}>
                            Got it
                        </button>
                    </div>
                </div>
            )}

            {showRewardPopup && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>Voyage completed</h2>
                        <p>You successfully completed your transport order.</p>
                        <h1>+{rewardAmount} Coins</h1>

                        <button onClick={() => setShowRewardPopup(false)}>
                            Nice
                        </button>
                    </div>
                </div>
            )}

        </div>
    );
}

export default GamePage;