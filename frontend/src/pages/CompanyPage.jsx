import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import api from "../api/api";
import cheapSide from "../assets/ships/cheapSide.png";
import middleSide from "../assets/ships/middleSide.png";
import expensiveSide from "../assets/ships/expensiveSide.png";
import "./CompanyPage.css";

function CompanyPage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [session, setSession] = useState(null);
    const [voyages, setVoyages] = useState([]);

    const storedPlayer = JSON.parse(sessionStorage.getItem(`player-${sessionCode}`));

    const fetchData = async () => {
        try {
            const sessionResponse = await api.get(`/sessions/${sessionCode}`);
            const sessionData = sessionResponse.data;

            const voyagesResponse = await api.get(`/voyages?sessionId=${sessionData.id}`);

            setSession(sessionData);
            setVoyages(voyagesResponse.data);
        } catch (error) {
            console.error("Failed to fetch company data:", error);
        }
    };

    useEffect(() => {
        fetchData();
    }, [sessionCode]);

    useEffect(() => {
        const socket = new SockJS(`${import.meta.env.VITE_API_BASE_URL}/ws`);

        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000
        });

        client.onConnect = () => {
            console.log("CompanyPage WebSocket connected");

            client.subscribe(`/topic/session/${sessionCode}`, async (message) => {
                const data = JSON.parse(message.body);
                console.log("COMPANY WS EVENT:", data);

                if (data.type === "TICK") {
                    await fetchData();
                    return;
                }

                if (data.type === "VOYAGE_STARTED") {
                    await fetchData();
                    return;
                }

                if (data.type === "VOYAGE_FINISHED") {
                    await fetchData();
                    return;
                }
            });
        };

        client.activate();

        return () => {
            client.deactivate();
        };
    }, [sessionCode]);

    const currentPlayer = session?.players?.find(
        (player) => player.id === storedPlayer?.id
    );

    if (!session) {
        return <div style={{ color: "white", padding: "20px" }}>Loading company...</div>;
    }

    if (!currentPlayer) {
        return <div style={{ color: "white", padding: "20px" }}>Player not found in this session.</div>;
    }

    const getShipImage = (type) => {
        if (type === "CHEAP") return cheapSide;
        if (type === "MEDIUM") return middleSide;
        return expensiveSide;
    };

    const getShipDisplayName = (type) => {
        if (type === "CHEAP") return "Cutter";
        if (type === "MEDIUM") return "Brigantine";
        if (type === "EXPENSIVE") return "Galleon";
        return type;
    };

    const getRunningVoyageForShip = (shipId) => {
        return voyages.find(
            (voyage) => voyage.shipId === shipId && voyage.status === "RUNNING"
        );
    };

    const getVoyageProgress = (voyage) => {
        if (!voyage) return null;

        return {
            current: voyage.currentDay,
            total: voyage.duration
        };
    };

    const getShipLocationText = (ship) => {
        const runningVoyage = getRunningVoyageForShip(ship.id);

        if (runningVoyage) {
            const origin =
                runningVoyage.originPort?.name ||
                runningVoyage.originPort ||
                ship.currentPort ||
                "Unknown";

            const destination =
                runningVoyage.destinationPort?.name ||
                runningVoyage.destinationPort ||
                "Unknown";

            return `${origin} → ${destination}`;
        }

        return ship.currentPort || "Unknown";
    };

    return (
        <div className="company-page">
            <div className="company-overlay"></div>

            <div className="company-content">
                <div className="company-topbar">
                    <button
                        className="back-button"
                        onClick={() => navigate(`/game/${sessionCode}`)}
                    >
                        ← Back
                    </button>
                </div>

                <header className="company-header">
                    <h1>Company</h1>
                    <p>Overview of your company and fleet.</p>
                </header>

                <div className="company-summary">
                    <div className="summary-card">
                        <h3>Company Name</h3>
                        <p>{currentPlayer.companyName || "Not set yet"}</p>
                    </div>

                    <div className="summary-card">
                        <h3>Balance</h3>
                        <p>${currentPlayer.balance}</p>
                    </div>
                </div>

                <div className="fleet-section">
                    <h2>Your Ships</h2>

                    {!currentPlayer.ships || currentPlayer.ships.length === 0 ? (
                        <p className="empty-text">No ships owned yet.</p>
                    ) : (
                        <div className="fleet-list">
                            {currentPlayer.ships.map((ship) => (
                                <div key={ship.id} className="fleet-card">
                                    <div className="fleet-image-box">
                                        <img src={getShipImage(ship.type)} alt={ship.name} />
                                    </div>

                                    <div className="fleet-info">
                                        <h3>{ship.name}</h3>

                                        <p className="ship-type">{getShipDisplayName(ship.type)}</p>
                                        <p className="ship-location">
                                            {getShipLocationText(ship)}
                                        </p>

                                        {(() => {
                                            const voyage = getRunningVoyageForShip(ship.id);
                                            const progress = getVoyageProgress(voyage);

                                            if (!progress) return null;

                                            const percent = (progress.current / progress.total) * 100;

                                            return (
                                                <div className="voyage-progress">
                                                    <span>Voyage Progress</span>
                                                    <div className="bar">
                                                        <div style={{ width: `${percent}%` }} />
                                                        <span className="bar-text">
                                                            {progress.current} / {progress.total}
                                                        </span>
                                                    </div>
                                                </div>
                                            );
                                        })()}

                                        <div className="ship-stats">
                                            <div className="stat-block">
                                                <span className="stat-label">Fuel</span>
                                                <div className="bar small">
                                                    <div
                                                        style={{
                                                            width: `${ship.fuelLevel}%`,
                                                            background: ship.fuelLevel < 20 ? "#ef4444" :
                                                                ship.fuelLevel < 50 ? "#f59e0b" :
                                                                    "#22c55e"
                                                        }}
                                                    />
                                                    <span className="bar-text">{ship.fuelLevel?.toFixed(0)}%</span>
                                                </div>
                                            </div>

                                            <div className="stat-block">
                                                <span className="stat-label">Condition</span>
                                                <div className="bar small">
                                                    <div
                                                        style={{
                                                            width: `${ship.condition}%`,
                                                            background: ship.condition < 20 ? "#ef4444" :
                                                                ship.condition < 50 ? "#f59e0b" :
                                                                    "#22c55e"
                                                        }}
                                                    />
                                                    <span className="bar-text">{ship.condition?.toFixed(0)}%</span>
                                                </div>
                                            </div>

                                            <div className="stat-block">
                                                <span className="stat-label">Capacity</span>
                                                <div className="bar small capacity-bar">
                                                    <div
                                                        style={{
                                                            width: `${((ship.cargoCapacity - (ship.usedCapacity || 0)) / ship.cargoCapacity) * 100}%`
                                                        }}
                                                    />
                                                    <span className="bar-text">
                                                        {ship.cargoCapacity - (ship.usedCapacity || 0)} / {ship.cargoCapacity}
                                                    </span>
                                                </div>
                                            </div>

                                            <div className="stat-block">
                                                <span className="stat-label">Speed</span>
                                                <span className="stat-value">{ship.speed}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default CompanyPage;