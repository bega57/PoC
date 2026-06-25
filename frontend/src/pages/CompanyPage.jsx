import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import cheapSide from "../assets/ships/cheapSide.png";
import middleSide from "../assets/ships/middleSide.png";
import expensiveSide from "../assets/ships/expensiveSide.png";
import "./CompanyPage.css";
import { useContext } from "react";
import { GameContext } from "../layouts/AppLayout";

function CompanyPage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const { session } = useContext(GameContext);
    const [voyages, setVoyages] = useState([]);

    const lastTickTimeRef = useRef(Date.now());
    const [subTickFraction, setSubTickFraction] = useState(0);
    const lastProgressRef = useRef({});

    const storedPlayer = JSON.parse(sessionStorage.getItem(`player-${sessionCode}`));

    const [ships, setShips] = useState([]);

    const getBarColor = (value) => {
        if (value <= 20) return "#ef4444";
        if (value <= 50) return "#f59e0b";
        return "#22c55e";
    };


    const fetchData = async () => {
        try {
            const sessionResponse = await api.get(`/sessions/${sessionCode}`);
            const sessionData = sessionResponse.data;

            const shipsResponse = await api.get(`/ships/player/${storedPlayer.id}`);
            setShips(shipsResponse.data);

            const voyagesResponse = await api.get(
                `/voyages?sessionId=${sessionData.id}&currentTick=${session.currentTick}`
            );

            setVoyages(voyagesResponse.data);
        } catch (error) {
            console.error("Failed to fetch company data:", error);
        }
    };

    useEffect(() => {
        if (!session) return;
        fetchData();
    }, [session]);

    useEffect(() => {
        lastTickTimeRef.current = Date.now();
        setSubTickFraction(0);
    }, [session?.currentTick]);

    useEffect(() => {
        const interval = setInterval(() => {
            const elapsed = Date.now() - lastTickTimeRef.current;
            setSubTickFraction(Math.min(1, elapsed / 5000));
        }, 100);
        return () => clearInterval(interval);
    }, []);

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
                        onClick={() => navigate(`/session/${sessionCode}/game`)}
                    >
                        🡸 Back to Game
                    </button>
                </div>

                <header className="company-header">
                    <h1>Company</h1>
                    <p>Overview of your company and fleet.</p>
                </header>

                <div className="fleet-section">
                    <h2>Your Ships</h2>

                    {ships.length === 0 ? (
                        <p className="empty-text">No ships owned yet.</p>
                    ) : (
                        <div className="fleet-list">
                            {ships.map((ship) => (
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

                                            const base = progress.current / Math.max(1, progress.total);
                                            const perTick = 1 / Math.max(1, progress.total);

                                            // Freeze when event is pending
                                            const frozen = voyage.eventTriggered && !voyage.eventResolved;
                                            const raw = frozen
                                                ? base
                                                : Math.min(1, base + subTickFraction * perTick);

                                            // Never go backwards
                                            const prev = lastProgressRef.current[voyage.id] ?? 0;
                                            const smooth = Math.max(prev, raw);
                                            lastProgressRef.current[voyage.id] = smooth;

                                            const percent = smooth * 100;

                                            return (
                                                <div className="voyage-progress">
                                                    <span>Voyage Progress</span>
                                                    <div className="bar">
                                                        <div style={{ width: `${percent}%`, background: "linear-gradient(90deg, #22c55e, #4ade80)" }} />
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
                                                            background: getBarColor(ship.condition)
                                                        }}
                                                    />

                                                    <span className="bar-text">
                                                        {ship.condition?.toFixed(0)}%
                                                    </span>
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