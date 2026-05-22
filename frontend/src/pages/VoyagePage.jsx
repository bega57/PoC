import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./VoyagePage.css";
import { useContext } from "react";
import { GameContext } from "../layouts/AppLayout";
import { useEffect, useMemo, useState, useRef } from "react";
import RetroModal from "../components/ui/RetroModal";

export default function VoyagePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [ships, setShips] = useState([]);
    const [selectedShip, setSelectedShip] = useState(null);
    const [allCargo, setAllCargo] = useState([]);
    const [selectedDestination, setSelectedDestination] = useState("");
    const [selectedCargoId, setSelectedCargoId] = useState("");
    const [voyages, setVoyages] = useState([]);
    const [showVoyageStartedPopup, setShowVoyageStartedPopup] = useState(false);
    const [startedVoyageInfo, setStartedVoyageInfo] = useState(null);
    const { session, player } = useContext(GameContext);
    const [errorMessage, setErrorMessage] = useState(null);
    const previousPortRef = useRef(null);

    // ==================== SMUGGLING STATE ====================
    const [showSmugglingOffer, setShowSmugglingOffer] = useState(false);
    // =========================================================

    const fetchVoyages = async () => {
        if (!session) return;

        try {
            const res = await api.get(
                `/voyages?sessionId=${session.id}&currentTick=${session.currentTick}`
            );
            setVoyages(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    const getBarColor = (value) => {
        if (value <= 20) return "#ef4444";   // red
        if (value <= 50) return "#f59e0b";   // orange
        return "#22c55e";                    // green
    };

    useEffect(() => {

        if (!player?.id) {
            alert("No active player found.");
            navigate(`/session/${sessionCode}/game`);
            return;
        }

        api.get(`/ships/player/${player.id}`)
            .then(res => {
                setShips(res.data);

                if (res.data.length > 0 && !selectedShip) {
                    setSelectedShip(res.data[0]);
                }
            })
            .catch(err => console.error(err));
    }, [sessionCode, player]);


    useEffect(() => {
        if (!player?.id) return;

        api.get(`/ships/player/${player.id}`)
            .then(res => {
                setShips(res.data);

                if (selectedShip) {
                    const updatedShip = res.data.find(
                        s => s.id === selectedShip.id
                    );

                    if (updatedShip) {
                        setSelectedShip(updatedShip);
                    }
                }
            })
            .catch(err => console.error(err));

    }, [session]);

    useEffect(() => {

        const currentPort = selectedShip?.currentPort;

        if (!currentPort) {
            setAllCargo([]);
            setSelectedDestination("");
            setSelectedCargoId("");
            return;
        }

        const portChanged =
            previousPortRef.current !== currentPort;

        previousPortRef.current = currentPort;

        api.get(`/cargo?portName=${encodeURIComponent(currentPort)}`)
            .then(res => {

                setAllCargo(res.data || []);

                if (portChanged) {
                    setSelectedDestination("");
                    setSelectedCargoId("");
                }

            })
            .catch(err => {
                console.error(err);

                setAllCargo([]);

                if (portChanged) {
                    setSelectedDestination("");
                    setSelectedCargoId("");
                }
            });

    }, [selectedShip]);

    useEffect(() => {
        fetchVoyages();
    }, [session]);

    const isShipBusy = (shipId) => {
        return voyages.some(
            v => v.shipId === shipId && v.status === "RUNNING"
        );
    };

    const availableDestinations = useMemo(() => {
        const names = allCargo
            .map(item => item.destinationPort?.name)
            .filter(Boolean);

        return [...new Set(names)].sort((a, b) => a.localeCompare(b));
    }, [allCargo]);

    const filteredCargo = useMemo(() => {
        if (!selectedDestination) return [];

        return allCargo.filter(
            item => item.destinationPort?.name === selectedDestination
        );
    }, [allCargo, selectedDestination]);

    const selectedCargo = useMemo(() => {
        return filteredCargo.find(item => item.id === Number(selectedCargoId)) || null;
    }, [filteredCargo, selectedCargoId]);

    const VAT = 0.2;
    const toGross = (price) => Math.round(price * (1 + VAT));

    const grossPrice = selectedCargo ? toGross(selectedCargo.price) : 0;

    const formatNumber = (value) => {
        return Number(value).toLocaleString("de-DE");
    };

    // ==================== SMUGGLING: actual start with flag ====================
    const startVoyageWithSmuggling = async (smuggling) => {
        try {
            await api.post("/voyages/start", {
                shipId: selectedShip.id,
                cargoId: Number(selectedCargoId),
                sessionId: session.id,
                currentTick: session.currentTick,
                smuggling: smuggling
            });

            await fetchVoyages();

            setStartedVoyageInfo({
                shipName: selectedShip.name,
                origin: selectedShip.currentPort,
                destination: selectedCargo?.destinationPort?.name,
                cargoName: selectedCargo?.name,
                cargoType: selectedCargo?.type,
                duration: selectedCargo.requiredTicks,
                price: selectedCargo?.price,
                reward: selectedCargo?.reward,
                smuggling: smuggling
            });

            setShowVoyageStartedPopup(true);
        } catch (err) {
            console.error(err);
            setErrorMessage(
                err.response?.data?.message ||
                err.response?.data ||
                "Failed to start voyage"
            );
        }
    };
    // ==========================================================================

    const handleStartVoyage = async () => {
        if (availableDestinations.length === 0) {
            setErrorMessage("No cargo available from this port");
            return;
        }
        if (!selectedShip) {
            alert("Select a ship");
            return;
        }

        if (isShipBusy(selectedShip.id)) {
            alert("This ship is already traveling");
            return;
        }

        if (!selectedDestination) {
            alert("Select a destination");
            return;
        }

        if (!selectedCargoId) {
            alert("Select a cargo order");
            return;
        }

        // ==================== SMUGGLING OFFER (~30% chance) ====================
        const offerSmuggling = Math.random() < 0.3;
        if (offerSmuggling) {
            setShowSmugglingOffer(true);
            return;
        }
        // =======================================================================

        await startVoyageWithSmuggling(false);
    };

    const capacity = selectedShip?.cargoCapacity || 0;
    const used = selectedShip?.usedCapacity || 0;

    const freeCapacity = capacity - used;
    const percent = capacity > 0 ? (freeCapacity / capacity) * 100 : 0;

    const hasEnoughCapacity =
        selectedShip && selectedCargo &&
        freeCapacity >= selectedCargo.requiredCapacity;

    return (
        <div className="voyage-page">
            <div className="voyage-overlay"></div>

            <div className="voyage-content">

                <div className="voyage-topbar">
                    <button
                        className="back-button"
                        onClick={() => navigate(-1)}
                    >
                        🡸 Back to Game
                    </button>
                </div>

                <header className="voyage-header">
                    <h1>Plan Your Next Voyage</h1>

                    <p>
                        Choose a ship, assign cargo and send your fleet across the seas.
                    </p>
                </header>

                <div className="active-voyages-banner">

                    <h3>Active Voyages</h3>

                    <div className="active-voyages-list">

                        {voyages.filter(v => v.status === "RUNNING").length === 0 ? (

                            <p>No ships currently at sea.</p>

                        ) : (

                            voyages
                                .filter(v => v.status === "RUNNING")
                                .map(v => {

                                    console.log(v);

                                    return (

                                        <div
                                            key={v.id}
                                            className="active-voyage-card"
                                        >

                                            <div className="active-voyage-top">

                                                <span className="voyage-live-dot"></span>

                                                <span className="voyage-live-text">
                            EN ROUTE
                        </span>

                                            </div>

                                            <h4>
                                                {v.shipName}
                                            </h4>

                                            <p>
                                                {v.originPort || "Unknown"} → {v.destinationPort || "Unknown"}
                                            </p>

                                        </div>

                                    );

                                })
                        )}

                    </div>

                </div>

                <div className="voyage-cards">
                    <div className="voyage-card ship-card-area">
                        <h2>1. Choose Ship</h2>

                        <select
                            value={selectedShip?.id || ""}
                            onChange={(e) => {
                                const ship = ships.find(
                                    s => s.id === Number(e.target.value)
                                );
                                setSelectedShip(ship || null);
                            }}
                        >
                            {ships.length === 0 ? (
                                <option value="">No ships available</option>
                            ) : (
                                ships.map(ship => (
                                    <option
                                        key={ship.id}
                                        value={ship.id}
                                    >
                                        {ship.name} {isShipBusy(ship.id) ? "🚫 (busy)" : ""}
                                    </option>
                                ))
                            )}
                        </select>

                        {selectedShip && (
                            <div className="voyage-info">

                                <div className="selected-ship-banner">

                                    <div>

                                        <span
                                            className={`ship-status ${
                                                isShipBusy(selectedShip.id)
                                                    ? "busy"
                                                    : "ready"
                                            }`}
                                        >
                                            {isShipBusy(selectedShip.id)
                                                ? "BUSY"
                                                : "READY"}
                                        </span>

                                        <h3>{selectedShip.name}</h3>

                                        <p>
                                            Docked at {selectedShip.currentPort}
                                        </p>

                                    </div>

                                </div>

                                <div className="stat-bar">
                                    <div className="stat-header">
                                        <span>Fuel</span>
                                    </div>

                                    <div className="bar">
                                        <div
                                            style={{
                                                width: `${selectedShip.fuelLevel}%`,
                                                background: getBarColor(selectedShip.fuelLevel)
                                            }}
                                        />

                                        <span className="bar-text">
                                        {selectedShip.fuelLevel?.toFixed(0)}%
                                        </span>
                                    </div>
                                </div>

                                <div className="stat-bar">
                                    <div className="stat-header">
                                        <span>Condition</span>
                                    </div>

                                    <div className="bar">
                                        <div
                                            style={{
                                                width: `${selectedShip.condition}%`,
                                                background: getBarColor(selectedShip.condition)
                                            }}
                                        />
                                        <span className="bar-text">
                                            {selectedShip.condition?.toFixed(0)}%
                                        </span>
                                    </div>
                                </div>

                                <div className="stat-bar">
                                    <div className="stat-header">
                                        <span>Capacity</span>
                                    </div>

                                    <div className="bar capacity-bar">
                                        <div style={{ width: `${percent}%` }} />
                                        <span className="bar-text">
                                            {freeCapacity} / {selectedShip.cargoCapacity}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    <div className="voyage-card destination-card-area">
                        <h2>2. Select Destination</h2>

                        {!selectedShip ? (
                            <p>Select a ship first</p>
                        ) : availableDestinations.length === 0 ? (
                            <p>No destinations available from {selectedShip?.currentPort || "Unknown"}</p>
                        ) : (
                            <>
                                <select
                                    value={selectedDestination}
                                    onChange={(e) => {
                                        setSelectedDestination(e.target.value);
                                        setSelectedCargoId("");
                                    }}
                                >
                                    <option value="">Select destination</option>
                                    {availableDestinations.map(destination => (
                                        <option key={destination} value={destination}>
                                            {destination}
                                        </option>
                                    ))}
                                </select>

                                {selectedDestination && (
                                    <p className="voyage-info">
                                        Selected destination: {selectedDestination}
                                    </p>
                                )}
                            </>
                        )}
                    </div>

                    <div className="voyage-card cargo-card-area">
                        <h2>3. Cargo Orders</h2>

                        {!selectedDestination ? (
                            <p>Select a destination first</p>
                        ) : filteredCargo.length === 0 ? (
                            <p style={{ color: "#f87171" }}>
                                🚫 No cargo available from {selectedShip?.currentPort}.
                            </p>
                        ) : (
                            <>
                                <select
                                    value={selectedCargoId}
                                    onChange={(e) => setSelectedCargoId(e.target.value)}
                                >
                                    <option value="">Select cargo order</option>
                                    {filteredCargo.map(item => (
                                        <option key={item.id} value={item.id}>
                                            {item.name}
                                        </option>
                                    ))}
                                </select>

                                <div style={{ marginTop: "15px" }}>
                                    {filteredCargo.map(item => (
                                        <div
                                            key={item.id}
                                            className={`cargo-card ${
                                                item.id === Number(selectedCargoId)
                                                    ? "selected"
                                                    : ""
                                            }`}
                                            style={{
                                                cursor: "pointer"
                                            }}
                                            onClick={() => setSelectedCargoId(String(item.id))}
                                        >
                                            <h4>{item.name}</h4>

                                            <p className="route">
                                                {item.originPort?.name} → {item.destinationPort?.name}
                                            </p>

                                            <div className="cargo-stats">
                                                <span>💰 {toGross(item.price)}</span>
                                                <span>🏆 {item.reward}</span>
                                                <span>⏱ {item.requiredTicks}</span>
                                                <span>📦 {item.type?.replaceAll("_", " ")}</span>
                                            </div>

                                            <p style={{ fontSize: "12px", opacity: 0.7 }}>
                                                {item.description}
                                            </p>
                                        </div>
                                    ))}
                                </div>
                            </>
                        )}
                    </div>

                    <div className="voyage-card summary">
                        <h2>4. Summary</h2>

                        {!selectedShip || !selectedCargo ? (
                            <p>Select ship, destination and cargo order</p>
                        ) : (
                            <div className="voyage-info">

                                <div className="voyage-route-hero">

                <span>
                    {selectedShip?.currentPort}
                </span>

                                    <div className="route-line">
                                        <div className="route-dot"></div>
                                    </div>

                                    <span>
                    {selectedCargo.destinationPort?.name}
                </span>

                                </div>

                                <div className="summary-grid">

                                    <div className="summary-item">
                                        <span>🚢 Ship</span>
                                        <strong>{selectedShip.name}</strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>📦 Cargo</span>
                                        <strong>{selectedCargo.name}</strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>📂 Type</span>
                                        <strong>
                                            {selectedCargo.type?.replaceAll("_", " ")}
                                        </strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>💰 Price</span>
                                        <strong>{formatNumber(grossPrice)} Talers</strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>🏆 Reward</span>
                                        <strong>{formatNumber(selectedCargo.reward)} Talers</strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>📈 Profit</span>
                                        <strong>
                                            {formatNumber(selectedCargo.reward - grossPrice)} Talers
                                        </strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>📦 Capacity</span>
                                        <strong>
                                            {selectedCargo.requiredCapacity}
                                        </strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>⏱ Duration</span>
                                        <strong>
                                            {selectedCargo.requiredTicks} days
                                        </strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>⛽ Fuel Consumption</span>
                                        <strong>
                                            {selectedCargo.fuelConsumption}%
                                        </strong>
                                    </div>

                                    <div className="summary-item">
                                        <span>🔧 Ship Deterioration</span>
                                        <strong>
                                            {selectedCargo.conditionDamage}%
                                        </strong>
                                    </div>

                                    <div className="summary-item full-width">
                                        <span>📝 Description</span>
                                        <strong>{selectedCargo.description}</strong>
                                    </div>

                                </div>

                                {!hasEnoughCapacity && (
                                    <p style={{ color: "#f87171", marginTop: "10px" }}>
                                        This ship does not have enough cargo capacity for the selected order.
                                    </p>
                                )}

                            </div>
                        )}
                    </div>

                </div>

                <div style={{ marginTop: "30px", textAlign: "center" }}>
                    <button
                        className="start-button"
                        onClick={handleStartVoyage}
                        disabled={
                            !selectedShip ||
                            !selectedDestination ||
                            !selectedCargoId ||
                            availableDestinations.length === 0 ||
                            isShipBusy(selectedShip?.id) ||
                            !hasEnoughCapacity
                        }
                    >
                        Start Voyage
                    </button>
                </div>
            </div>

            {/* ==================== SMUGGLING OFFER DIALOG ==================== */}
            {showSmugglingOffer && (
                <RetroModal
                    title="Smuggling Offer"
                    onClose={() => {
                        setShowSmugglingOffer(false);
                        startVoyageWithSmuggling(false);
                    }}
                >
                    <p>A shady figure approaches you at the dock...</p>
                    <p>
                        "Psst... Want to smuggle some 'special cargo'
                        to {selectedCargo?.destinationPort?.name}?
                        I'll pay you an extra{" "}
                        {formatNumber(Math.round((selectedCargo?.reward || 0) * 0.3))} Talers
                        if you don't get caught..."
                    </p>

                    <button
                        className="retro-button"
                        onClick={() => {
                            setShowSmugglingOffer(false);
                            startVoyageWithSmuggling(true);
                        }}
                    >
                        Accept Smuggling
                    </button>

                    <button
                        className="retro-button secondary"
                        onClick={() => {
                            setShowSmugglingOffer(false);
                            startVoyageWithSmuggling(false);
                        }}
                    >
                        Decline
                    </button>
                </RetroModal>
            )}
            {/* ================================================================= */}

            {showVoyageStartedPopup && (
                <RetroModal
                    title="Voyage Started"
                    onClose={() => setShowVoyageStartedPopup(false)}
                >
                    <p>Your ship is now on its way.</p>

                    {startedVoyageInfo && (
                        <>
                            <p>Ship: {startedVoyageInfo.shipName}</p>
                            <p>Route: {startedVoyageInfo.origin} → {startedVoyageInfo.destination}</p>
                            <p>
                                Duration: {startedVoyageInfo.duration} days
                            </p>
                            <p>Paid: {startedVoyageInfo.price} Talers</p>
                            <p>Potential Reward: {startedVoyageInfo.reward} Talers</p>
                            {startedVoyageInfo.smuggling && (
                                <p style={{ color: "#f59e0b" }}>
                                    🤫 Smuggling cargo on board!
                                </p>
                            )}
                        </>
                    )}

                    <button
                        className="retro-button"
                        onClick={() => navigate(`/session/${sessionCode}/game`)}
                    >
                        Back to Game
                    </button>
                </RetroModal>
            )}

            {errorMessage && (
                <RetroModal
                    title="Error"
                    onClose={() => setErrorMessage(null)}
                >
                    <p>{errorMessage}</p>

                    <button
                        className="retro-button"
                        onClick={() => setErrorMessage(null)}
                    >
                        OK
                    </button>
                </RetroModal>
            )}


        </div>
    );
}
