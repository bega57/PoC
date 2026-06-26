import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./VoyagePage.css";
import { useContext, useRef } from "react";
import { GameContext } from "../layouts/AppLayout";
import { useEffect, useMemo, useState } from "react";
import voyageMusic from "../assets/music/voyage.mp3";
import imgEnergyDrink from "../assets/powerups/EnergyDrink.png";
import imgChocolateCake from "../assets/powerups/ChocolateCake.png";
import imgLuckyClover from "../assets/powerups/LuckyClover.png";
import imgTurboCable from "../assets/powerups/TurboCable.png";
import imgVipPass from "../assets/powerups/VipPass.png";
import imgCD from "../assets/powerups/CD.png";

const POWERUP_IMAGES = {
    RED_BULL:       imgEnergyDrink,
    CHOCOLATE_CAKE: imgChocolateCake,
    LUCKY_CLOVER:   imgLuckyClover,
    TURBO_CABLE:    imgTurboCable,
    VIP_PASS:       imgVipPass,
    MUSIC_PLAYER:   imgCD,
};
import RetroModal from "../components/ui/RetroModal";

export default function VoyagePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [activeTab, setActiveTab] = useState("WITH_CARGO");
    const [ships, setShips] = useState([]);
    const [selectedShip, setSelectedShip] = useState(null);
    const [allCargo, setAllCargo] = useState([]);
    const [selectedDestination, setSelectedDestination] = useState("");
    const [selectedCargoId, setSelectedCargoId] = useState("");
    const [voyages, setVoyages] = useState([]);
    const [showVoyageStartedPopup, setShowVoyageStartedPopup] = useState(false);
    const [startedVoyageInfo, setStartedVoyageInfo] = useState(null);
    const { session, player, playVoyageMusic, stopVoyageMusic } = useContext(GameContext);
    const [errorMessage, setErrorMessage] = useState(null);
    const previousPortRef = useRef(null);
    const [allPorts, setAllPorts] = useState([]);
    const [selectedEmptyDestination, setSelectedEmptyDestination] = useState("");
    const [showSmugglingOffer, setShowSmugglingOffer] = useState(false);
    const [inventory, setInventory] = useState([]);
    const [selectedPowerUp, setSelectedPowerUp] = useState(null);

    const getPowerUpStartMessage = (type) => {
        switch (type) {
            case "RED_BULL":       return "🐂 Red Bull activated — this voyage arrives 1 day early!";
            case "CHOCOLATE_CAKE": return "🍰 Chocolate Cake activated — you'll earn 50% bonus points on arrival!";
            case "LUCKY_CLOVER":   return "🍀 Lucky Clover activated — customs won't check your ship this voyage!";
            case "TURBO_CABLE":    return "⚡ Turbo Cable activated — event delays will be completely ignored!";
            case "VIP_PASS":       return "💎 VIP Pass activated — you'll earn 20% extra reward on arrival!";
            case "MUSIC_PLAYER":   return "🎵 Music Player activated — enjoy the tunes on your voyage!";
            default: return null;
        }
    };

    const getPowerUpEffect = (type) => {
        switch (type) {
            case "RED_BULL":       return "voyage arrives 1 day early";
            case "CHOCOLATE_CAKE": return "+50% bonus points on arrival";
            case "LUCKY_CLOVER":   return "customs won't check your ship";
            case "TURBO_CABLE":    return "event delays completely ignored";
            case "VIP_PASS":       return "+20% extra reward on arrival";
            case "MUSIC_PLAYER":   return "enjoy some music on the way";
            default: return null;
        }
    };

    const fetchVoyages = async () => {
        if (!session) return;
        try {
            const res = await api.get(`/voyages?sessionId=${session.id}&currentTick=${session.currentTick}`);
            setVoyages(res.data);
        } catch (err) { console.error(err); }
    };

    const getBarColor = (value) => {
        if (value <= 20) return "#ef4444";
        if (value <= 50) return "#f59e0b";
        return "#22c55e";
    };

    useEffect(() => {
        if (!player?.id) { alert("No active player found."); navigate(`/session/${sessionCode}/game`); return; }
        api.get(`/ships/player/${player.id}`)
            .then(res => { setShips(res.data); if (res.data.length > 0 && !selectedShip) setSelectedShip(res.data[0]); })
            .catch(err => console.error(err));
    }, [sessionCode, player]);

    useEffect(() => {
        if (!player?.id) return;
        api.get(`/ships/player/${player.id}`)
            .then(res => {
                setShips(res.data);
                if (selectedShip) {
                    const updated = res.data.find(s => s.id === selectedShip.id);
                    if (updated) setSelectedShip(updated);
                }
            })
            .catch(err => console.error(err));
    }, [session]);

    useEffect(() => {
        const currentPort = selectedShip?.currentPort;
        if (!currentPort) { setAllCargo([]); setSelectedDestination(""); setSelectedCargoId(""); return; }
        const portChanged = previousPortRef.current !== currentPort;
        previousPortRef.current = currentPort;
        api.get(`/cargo?portName=${encodeURIComponent(currentPort)}`)
            .then(res => { setAllCargo(res.data || []); if (portChanged) { setSelectedDestination(""); setSelectedCargoId(""); } })
            .catch(err => { console.error(err); setAllCargo([]); if (portChanged) { setSelectedDestination(""); setSelectedCargoId(""); } });
    }, [selectedShip]);

    useEffect(() => { fetchVoyages(); }, [session]);

    useEffect(() => {
        api.get("/ports").then(res => setAllPorts(res.data || [])).catch(err => console.error(err));
    }, []);

    useEffect(() => {
        if (player?.id) {
            api.get(`/shop/inventory/${player.id}`).then(res => setInventory(res.data || [])).catch(() => {});
        }
    }, [player?.id]);

    const isShipBusy = (shipId) => voyages.some(v => v.shipId === shipId && v.status === "RUNNING");

    const availableDestinations = useMemo(() => {
        const names = allCargo.map(item => item.destinationPort?.name).filter(Boolean);
        return [...new Set(names)].sort((a, b) => a.localeCompare(b));
    }, [allCargo]);

    const filteredCargo = useMemo(() => {
        if (!selectedDestination) return [];
        return allCargo.filter(item => item.destinationPort?.name === selectedDestination);
    }, [allCargo, selectedDestination]);

    const selectedCargo = useMemo(() => filteredCargo.find(item => item.id === Number(selectedCargoId)) || null, [filteredCargo, selectedCargoId]);

    const VAT = 0.2;
    const toGross = (price) => Math.round(price * (1 + VAT));
    const grossPrice = selectedCargo ? toGross(selectedCargo.price) : 0;
    const formatNumber = (value) => Number(value).toLocaleString("de-DE");

    const calcEstimatedPoints = (reward, riskLevel) => {
        const base = Math.floor(reward / 100);
        const multiplier = riskLevel === "HIGH" ? 2.0 : riskLevel === "MEDIUM" ? 1.5 : 1.0;
        return Math.floor(base * multiplier);
    };

    const startVoyageWithSmuggling = async (smuggling) => {
        try {
            await api.post("/voyages/start", {
                shipId: selectedShip.id,
                cargoId: Number(selectedCargoId),
                sessionId: session.id,
                currentTick: session.currentTick,
                smuggling,
                activePowerUp: selectedPowerUp || null
            });
            setSelectedPowerUp(null);
            if (player?.id) {
                api.get(`/shop/inventory/${player.id}`).then(res => setInventory(res.data || [])).catch(() => {});
            }
            if (selectedPowerUp === "MUSIC_PLAYER" && voyageMusic) {
                playVoyageMusic(voyageMusic, selectedShip?.id);
            }
            await fetchVoyages();
            setStartedVoyageInfo({ shipName: selectedShip.name, origin: selectedShip.currentPort, destination: selectedCargo?.destinationPort?.name, cargoName: selectedCargo?.name, cargoType: selectedCargo?.type, duration: selectedCargo.requiredTicks, price: selectedCargo?.price, reward: selectedCargo?.reward, smuggling, activePowerUp: selectedPowerUp });
            setShowVoyageStartedPopup(true);
        } catch (err) {
            console.error(err);
            let msg = "Failed to start voyage";
            const data = err.response?.data;
            if (typeof data === "string") { const match = data.match(/"message"\s*:\s*"([^"]+)"/); msg = match ? match[1] : data; }
            else if (data?.message) msg = data.message;
            else if (data?.error) msg = data.error;
            setErrorMessage(msg);
        }
    };

    const handleStartEmptyVoyage = async () => {
        if (!selectedShip) { alert("Select a ship"); return; }
        if (isShipBusy(selectedShip.id)) { alert("This ship is already traveling"); return; }
        if (!selectedEmptyDestination) { alert("Select a destination"); return; }
        try {
            await api.post("/voyages/start-empty", { shipId: selectedShip.id, destinationPortName: selectedEmptyDestination, sessionId: session.id, currentTick: session.currentTick });
            await fetchVoyages();
            setStartedVoyageInfo({ shipName: selectedShip.name, origin: selectedShip.currentPort, destination: selectedEmptyDestination, cargoName: null, duration: null, price: 0, reward: 0, smuggling: false });
            setShowVoyageStartedPopup(true);
        } catch (err) {
            console.error(err);
            let msg = "Failed to start voyage";
            const data = err.response?.data;
            if (typeof data === "string") { const match = data.match(/"message"\s*:\s*"([^"]+)"/); msg = match ? match[1] : data; }
            else if (data?.message) msg = data.message;
            setErrorMessage(msg);
        }
    };

    const handleStartVoyage = async () => {
        if (availableDestinations.length === 0) { setErrorMessage("No cargo available from this port"); return; }
        if (!selectedShip) { alert("Select a ship"); return; }
        if (isShipBusy(selectedShip.id)) { alert("This ship is already traveling"); return; }
        if (!selectedDestination && !selectedCargoId) return;
        if (!selectedDestination) return;
        if (!selectedCargoId) return;
        if (selectedCargo && selectedShip && selectedShip.fuelLevel < selectedCargo.fuelConsumption) { setErrorMessage("Not enough fuel for this voyage. Refuel your ship first."); return; }
        const offerSmuggling = Math.random() < 0.3;
        if (offerSmuggling) { setShowSmugglingOffer(true); return; }
        await startVoyageWithSmuggling(false);
    };

    const capacity = selectedShip?.cargoCapacity || 0;
    const used = selectedShip?.usedCapacity || 0;
    const freeCapacity = capacity - used;

    // ==================== EMPTY VOYAGE ESTIMATES ====================
    const emptyVoyageEstimate = useMemo(() => {
        if (!selectedShip || !selectedEmptyDestination) return null;
        const origin = allPorts.find(p => p.name === selectedShip.currentPort);
        const dest = allPorts.find(p => p.name === selectedEmptyDestination);
        if (!origin || !dest) return null;

        const dx = origin.latitude - dest.latitude;
        const dy = origin.longitude - dest.longitude;
        const distance = Math.sqrt(dx * dx + dy * dy) * 100;

        const tickModifier = selectedShip.speedCategory === "SLOW" ? 2 : selectedShip.speedCategory === "FAST" ? -1 : 0;
        const ticks = Math.max(2, Math.ceil(distance / 1400) + tickModifier);
        const fuel = Math.round(distance * 0.0004 * 1.2 * 100) / 100;
        const condition = Math.round(ticks * 0.08 * 0.8 * 100) / 100;
        return { ticks, fuel, condition };
    }, [selectedShip, selectedEmptyDestination, allPorts]);
    // ================================================================
    const percent = capacity > 0 ? (freeCapacity / capacity) * 100 : 0;
    const hasEnoughCapacity = selectedShip && selectedCargo && freeCapacity >= selectedCargo.requiredCapacity;

    return (
        <div className="voyage-page">
            <div className="voyage-overlay"></div>
            <div className="voyage-content">

                <div className="voyage-topbar">
                    <button className="back-button" onClick={() => navigate(-1)}>🡸 Back to Game</button>
                </div>

                <header className="voyage-header">
                    <h1>Plan Your Next Voyage</h1>
                    <p>Choose a ship, assign cargo and send your fleet across the seas.</p>
                </header>

                <div className="voyage-tabs">
                    <button className={`voyage-tab ${activeTab === "WITH_CARGO" ? "active" : ""}`} onClick={() => setActiveTab("WITH_CARGO")}>
                        📦 With Cargo
                    </button>
                    <button className={`voyage-tab ${activeTab === "EMPTY" ? "active" : ""}`} onClick={() => setActiveTab("EMPTY")}>
                        ⚓ Empty Voyage
                    </button>
                </div>

                <div className="active-voyages-banner">
                    <h3>Active Voyages</h3>
                    <div className="active-voyages-list">
                        {voyages.filter(v => v.status === "RUNNING").length === 0 ? (
                            <p>No ships currently at sea.</p>
                        ) : (
                            voyages.filter(v => v.status === "RUNNING").map(v => (
                                <div key={v.id} className="active-voyage-card">
                                    <div className="active-voyage-top">
                                        <span className="voyage-live-dot"></span>
                                        <span className="voyage-live-text">EN ROUTE</span>
                                    </div>
                                    <h4>{v.shipName}</h4>
                                    <p>{v.originPort || "Unknown"} → {v.destinationPort || "Unknown"}</p>
                                </div>
                            ))
                        )}
                    </div>
                </div>

                {activeTab === "EMPTY" && (<>
                    <div className="voyage-cards">
                        <div className="voyage-card ship-card-area">
                            <h2>1. Choose Ship</h2>
                            <select value={selectedShip?.id || ""} onChange={(e) => { const ship = ships.find(s => s.id === Number(e.target.value)); setSelectedShip(ship || null); }}>
                                {ships.length === 0 ? <option value="">No ships available</option> : ships.map(ship => (
                                    <option key={ship.id} value={ship.id}>{ship.name} {isShipBusy(ship.id) ? "🚫 (busy)" : ""}</option>
                                ))}
                            </select>
                            {selectedShip && (
                                <div className="voyage-info">
                                    <div className="selected-ship-banner">
                                        <div>
                                            <span className={`ship-status ${isShipBusy(selectedShip.id) ? "busy" : "ready"}`}>{isShipBusy(selectedShip.id) ? "BUSY" : "READY"}</span>
                                            <h3>{selectedShip.name}</h3>
                                            <p>Docked at {selectedShip.currentPort}</p>
                                        </div>
                                    </div>
                                    <div className="stat-bar">
                                        <div className="stat-header"><span>Fuel</span></div>
                                        <div className="bar">
                                            <div style={{ width: `${selectedShip.fuelLevel}%`, background: getBarColor(selectedShip.fuelLevel) }} />
                                            <span className="bar-text">{selectedShip.fuelLevel?.toFixed(0)}%</span>
                                        </div>
                                    </div>
                                    <div className="stat-bar">
                                        <div className="stat-header"><span>Condition</span></div>
                                        <div className="bar">
                                            <div style={{ width: `${selectedShip.condition}%`, background: getBarColor(selectedShip.condition) }} />
                                            <span className="bar-text">{selectedShip.condition?.toFixed(0)}%</span>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>

                        <div className="voyage-card destination-card-area">
                            <h2>2. Select Destination</h2>
                            <div className="empty-voyage-notice">
                                ⚠️ Empty voyages earn no reward. You only pay fuel and ship wear. Use this to reposition your ship.
                            </div>
                            {!selectedShip ? <p>Select a ship first</p> : (
                                <select value={selectedEmptyDestination} onChange={(e) => setSelectedEmptyDestination(e.target.value)}>
                                    <option value="">Select destination</option>
                                    {allPorts.filter(p => p.name !== selectedShip?.currentPort).sort((a, b) => a.name.localeCompare(b.name)).map(port => (
                                        <option key={port.name} value={port.name}>{port.name}</option>
                                    ))}
                                </select>
                            )}
                        </div>

                        <div className="voyage-card summary">
                            <h2>3. Summary</h2>
                            {!selectedShip || !selectedEmptyDestination ? <p>Select a ship and destination</p> : (
                                <div className="voyage-info">
                                    <div className="voyage-route-hero">
                                        <span>{selectedShip.currentPort}</span>
                                        <div className="route-line"><div className="route-dot"></div></div>
                                        <span>{selectedEmptyDestination}</span>
                                    </div>
                                    <div className="summary-grid">
                                        <div className="summary-item"><span>🚢 Ship</span><strong>{selectedShip.name}</strong></div>
                                        <div className="summary-item"><span>📦 Cargo</span><strong>None (Empty)</strong></div>
                                        <div className="summary-item"><span>💰 Price</span><strong>0 Coins</strong></div>
                                        <div className="summary-item"><span>🏆 Reward</span><strong>0 Coins</strong></div>
                                        <div className="summary-item"><span>⏱ Duration</span><strong>{emptyVoyageEstimate?.ticks ?? "—"} days</strong></div>
                                        <div className="summary-item"><span>⛽ Fuel Consumption</span><strong>{emptyVoyageEstimate?.fuel ?? "—"}%</strong></div>
                                        <div className="summary-item"><span>🔧 Ship Deterioration</span><strong>{emptyVoyageEstimate?.condition ?? "—"}%</strong></div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    <div style={{ marginTop: "30px", textAlign: "center" }}>
                        <button className="start-button" onClick={handleStartEmptyVoyage} disabled={!selectedShip || !selectedEmptyDestination || isShipBusy(selectedShip?.id)}>
                            Start Empty Voyage
                        </button>
                    </div>
                </>)}

                {activeTab === "WITH_CARGO" && (<>
                    <div className="voyage-cards">
                        <div className="voyage-card ship-card-area">
                            <h2>1. Choose Ship</h2>
                            <select value={selectedShip?.id || ""} onChange={(e) => { const ship = ships.find(s => s.id === Number(e.target.value)); setSelectedShip(ship || null); }}>
                                {ships.length === 0 ? <option value="">No ships available</option> : ships.map(ship => (
                                    <option key={ship.id} value={ship.id}>{ship.name} {isShipBusy(ship.id) ? "🚫 (busy)" : ""}</option>
                                ))}
                            </select>

                            {selectedShip && (
                                <div className="voyage-info">
                                    <div className="selected-ship-banner">
                                        <div>
                                            <span className={`ship-status ${isShipBusy(selectedShip.id) ? "busy" : "ready"}`}>{isShipBusy(selectedShip.id) ? "BUSY" : "READY"}</span>
                                            <h3>{selectedShip.name}</h3>
                                            <p>Docked at {selectedShip.currentPort}</p>
                                        </div>
                                    </div>
                                    <div className="stat-bar">
                                        <div className="stat-header"><span>Fuel</span></div>
                                        <div className="bar">
                                            <div style={{ width: `${selectedShip.fuelLevel}%`, background: getBarColor(selectedShip.fuelLevel) }} />
                                            <span className="bar-text">{selectedShip.fuelLevel?.toFixed(0)}%</span>
                                        </div>
                                    </div>
                                    <div className="stat-bar">
                                        <div className="stat-header"><span>Condition</span></div>
                                        <div className="bar">
                                            <div style={{ width: `${selectedShip.condition}%`, background: getBarColor(selectedShip.condition) }} />
                                            <span className="bar-text">{selectedShip.condition?.toFixed(0)}%</span>
                                        </div>
                                    </div>
                                    <div className="stat-bar">
                                        <div className="stat-header"><span>Capacity</span></div>
                                        <div className="bar capacity-bar">
                                            <div style={{ width: `${percent}%` }} />
                                            <span className="bar-text">{freeCapacity} / {selectedShip.cargoCapacity}</span>
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
                                    <select value={selectedDestination} onChange={(e) => { setSelectedDestination(e.target.value); setSelectedCargoId(""); }}>
                                        <option value="">Select destination</option>
                                        {availableDestinations.map(destination => (
                                            <option key={destination} value={destination}>{destination}</option>
                                        ))}
                                    </select>
                                    {selectedDestination && <p className="voyage-info">Selected destination: {selectedDestination}</p>}
                                </>
                            )}
                        </div>

                        <div className="voyage-card cargo-card-area">
                            <h2>3. Cargo Orders</h2>
                            {!selectedDestination ? (
                                <p>Select a destination first</p>
                            ) : filteredCargo.length === 0 ? (
                                <p style={{ color: "#f87171" }}>🚫 No cargo available from {selectedShip?.currentPort}.</p>
                            ) : (
                                <>
                                    <select value={selectedCargoId} onChange={(e) => setSelectedCargoId(e.target.value)}>
                                        <option value="">Select cargo order</option>
                                        {filteredCargo.map(item => <option key={item.id} value={item.id}>{item.name}</option>)}
                                    </select>
                                    <div style={{ marginTop: "15px" }}>
                                        {filteredCargo.map(item => (
                                            <div
                                                key={item.id}
                                                className={`cargo-card ${item.id === Number(selectedCargoId) ? "selected" : ""}`}
                                                style={{ cursor: "pointer" }}
                                                onClick={() => setSelectedCargoId(String(item.id))}
                                            >
                                                <h4>{item.name}</h4>
                                                <p className="route">{item.originPort?.name} → {item.destinationPort?.name}</p>
                                                <div className="cargo-stats">
                                                    <span>💰 {toGross(item.price)}</span>
                                                    <span>🏆 {item.reward}</span>
                                                    <span>⏱ {item.requiredTicks}</span>
                                                    <span>📦 {item.type?.replaceAll("_", " ")}</span>
                                                    <span style={{ color: "#facc15" }}>⭐ {calcEstimatedPoints(item.reward, item.riskLevel)} pts</span>
                                                </div>
                                                <p style={{ fontSize: "12px", opacity: 0.7 }}>{item.description}</p>
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
                                        <span>{selectedShip?.currentPort}</span>
                                        <div className="route-line"><div className="route-dot"></div></div>
                                        <span>{selectedCargo.destinationPort?.name}</span>
                                    </div>
                                    <div className="summary-grid">
                                        <div className="summary-item"><span>🚢 Ship</span><strong>{selectedShip.name}</strong></div>
                                        <div className="summary-item"><span>📦 Cargo</span><strong>{selectedCargo.name}</strong></div>
                                        <div className="summary-item"><span>📂 Type</span><strong>{selectedCargo.type?.replaceAll("_", " ")}</strong></div>
                                        <div className="summary-item"><span>💰 Price</span><strong>{formatNumber(grossPrice)} Coins</strong></div>
                                        <div className="summary-item"><span>🏆 Reward</span><strong>{formatNumber(selectedCargo.reward)} Coins</strong></div>
                                        <div className="summary-item"><span>📈 Profit</span><strong>{formatNumber(selectedCargo.reward - grossPrice)} Coins</strong></div>
                                        <div className="summary-item"><span>⭐ Est. Points</span><strong style={{ color: "#facc15" }}>{calcEstimatedPoints(selectedCargo.reward, selectedCargo.riskLevel)} pts</strong></div>
                                        <div className="summary-item"><span>📦 Capacity</span><strong>{selectedCargo.requiredCapacity}</strong></div>
                                        <div className="summary-item"><span>⏱ Duration</span><strong>{selectedCargo.requiredTicks} days</strong></div>
                                        <div className="summary-item"><span>⛽ Fuel Consumption</span><strong>{selectedCargo.fuelConsumption}%</strong></div>
                                        <div className="summary-item"><span>🔧 Ship Deterioration</span><strong>{selectedCargo.conditionDamage}%</strong></div>
                                        <div className="summary-item full-width"><span>📝 Description</span><strong>{selectedCargo.description}</strong></div>
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

                    {inventory.length > 0 && (
                        <div className="powerup-panel">
                            <h3 className="powerup-title">⚡ Activate a Power-Up</h3>
                            <div className="powerup-list">
                                {inventory.map(item => (
                                    <button
                                        key={item.type}
                                        className={`powerup-chip ${selectedPowerUp === item.type ? "selected" : ""}`}
                                        onClick={() => setSelectedPowerUp(prev => prev === item.type ? null : item.type)}
                                    >
                                        {POWERUP_IMAGES[item.type]
                                            ? <img src={POWERUP_IMAGES[item.type]} alt={item.displayName} style={{ width: "20px", height: "20px", objectFit: "contain", verticalAlign: "middle", marginRight: "6px" }} />
                                            : item.emoji + " "}
                                        {item.displayName} ×{item.quantity}
                                    </button>
                                ))}
                            </div>
                            {selectedPowerUp && (
                                <p className="powerup-active-hint">
                                    {POWERUP_IMAGES[selectedPowerUp]
                                        ? <img src={POWERUP_IMAGES[selectedPowerUp]} alt="" style={{ width: "18px", height: "18px", objectFit: "contain", verticalAlign: "middle", marginRight: "6px" }} />
                                        : inventory.find(i => i.type === selectedPowerUp)?.emoji + " "}
                                    <strong>{inventory.find(i => i.type === selectedPowerUp)?.displayName}</strong> will be used on this voyage
                                    {getPowerUpEffect(selectedPowerUp) && <> — {getPowerUpEffect(selectedPowerUp)}</>}.
                                </p>
                            )}
                        </div>
                    )}

                    <div style={{ marginTop: "30px", textAlign: "center" }}>
                        <button
                            className="start-button"
                            onClick={handleStartVoyage}
                            disabled={!selectedShip || !selectedDestination || !selectedCargoId || availableDestinations.length === 0 || isShipBusy(selectedShip?.id) || !hasEnoughCapacity}
                        >
                            Start Voyage
                        </button>
                        {selectedShip && !isShipBusy(selectedShip?.id) && (() => {
                            if (!selectedDestination && !selectedCargoId) return <p style={{ color: "#f87171", fontSize: "12px", marginTop: "8px" }}>Please select a destination and cargo first.</p>;
                            if (!selectedDestination) return <p style={{ color: "#f87171", fontSize: "12px", marginTop: "8px" }}>Please select a destination first.</p>;
                            if (!selectedCargoId) return <p style={{ color: "#f87171", fontSize: "12px", marginTop: "8px" }}>Please select a cargo order first.</p>;
                            return null;
                        })()}
                    </div>
                </>)}

            </div>

            {showSmugglingOffer && (
                <RetroModal title="Smuggling Offer" onClose={() => { setShowSmugglingOffer(false); startVoyageWithSmuggling(false); }}>
                    <p>A shady figure approaches you at the dock...</p>
                    <p>
                        "Psst... Want to smuggle some 'special cargo' to {selectedCargo?.destinationPort?.name}?
                        I'll pay you an extra {formatNumber(Math.round((selectedCargo?.reward || 0) * 0.3))} Coins if you don't get caught..."
                    </p>
                    <div style={{ background: "rgba(250,204,21,0.08)", border: "1px solid #facc15", borderRadius: "8px", padding: "10px 14px", margin: "10px 0", fontSize: "13px" }}>
                        <p style={{ margin: 0 }}>⭐ Base points: <strong>{calcEstimatedPoints(selectedCargo?.reward || 0, selectedCargo?.riskLevel)} pts</strong></p>
                        <p style={{ margin: "4px 0 0", color: "#facc15" }}>🤫 Smuggling bonus (if undetected): <strong>+{Math.floor(calcEstimatedPoints(selectedCargo?.reward || 0, selectedCargo?.riskLevel) * 0.5)} pts</strong></p>
                        <p style={{ margin: "4px 0 0", color: "#f87171" }}>🚨 Penalty if caught: <strong>-30 pts</strong></p>
                    </div>
                    <button className="retro-button" onClick={() => { setShowSmugglingOffer(false); startVoyageWithSmuggling(true); }}>Accept Smuggling</button>
                    <button className="retro-button secondary" onClick={() => { setShowSmugglingOffer(false); startVoyageWithSmuggling(false); }}>Decline</button>
                </RetroModal>
            )}

            {showVoyageStartedPopup && (
                <RetroModal title="Voyage Started" onClose={() => setShowVoyageStartedPopup(false)}>
                    <p>Your ship is now on its way.</p>
                    {startedVoyageInfo && (<>
                        <p>Ship: {startedVoyageInfo.shipName}</p>
                        <p>Route: {startedVoyageInfo.origin} → {startedVoyageInfo.destination}</p>
                        {startedVoyageInfo.duration && <p>Duration: {startedVoyageInfo.duration} days</p>}
                        {startedVoyageInfo.cargoName ? (<>
                            <p>Paid: {startedVoyageInfo.price} Coins</p>
                            <p>Potential Reward: {startedVoyageInfo.reward} Coins</p>
                        </>) : (
                            <p style={{ color: "#fde68a" }}>⚓ Empty voyage — no reward.</p>
                        )}
                        {startedVoyageInfo.smuggling && <p style={{ color: "#f59e0b" }}>🤫 Smuggling cargo on board!</p>}
                        {startedVoyageInfo.activePowerUp && (
                            <div style={{ marginTop: "12px", padding: "10px 14px", borderRadius: "10px", background: "rgba(168,85,247,0.12)", border: "1px solid rgba(168,85,247,0.35)", color: "#d8b4fe", fontSize: "14px" }}>
                                {getPowerUpStartMessage(startedVoyageInfo.activePowerUp)}
                            </div>
                        )}
                    </>)}
                    <button className="retro-button" onClick={() => { setShowVoyageStartedPopup(false); navigate(`/session/${sessionCode}/game`); }}>Back to Game</button>
                </RetroModal>
            )}

            {errorMessage && (
                <RetroModal title="Error" onClose={() => setErrorMessage(null)}>
                    <p>{errorMessage}</p>
                    <button className="retro-button" onClick={() => setErrorMessage(null)}>OK</button>
                </RetroModal>
            )}

        </div>
    );
}
