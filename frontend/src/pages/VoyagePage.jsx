import { useEffect, useMemo, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import api from "../api/api";
import "./VoyagePage.css";

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
    const [session, setSession] = useState(null);

    const fetchSession = async () => {
        try {
            const res = await api.get(`/sessions/${sessionCode}`);
            setSession(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    const fetchVoyages = async () => {
        if (!session) return;

        try {
            const res = await api.get(`/voyages?sessionId=${session.id}`);
            setVoyages(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchSession();
    }, [sessionCode]);

    useEffect(() => {
        const player = JSON.parse(sessionStorage.getItem(`player-${sessionCode}`));

        if (!player?.id) {
            alert("No active player found.");
            navigate(`/game/${sessionCode}`);
            return;
        }

        api.get(`/ships/player/${player.id}`)
            .then(res => {
                setShips(res.data);

                if (res.data.length > 0) {
                    setSelectedShip(res.data[0]);
                }
            })
            .catch(err => console.error(err));
    }, [sessionCode]);

    useEffect(() => {
        if (!session) return;
        fetchVoyages();
    }, [session]);

    useEffect(() => {
        if (!selectedShip?.currentPort) {
            setAllCargo([]);
            setSelectedDestination("");
            setSelectedCargoId("");
            return;
        }

        api.get(`/cargo?portName=${encodeURIComponent(selectedShip.currentPort)}`)
            .then(res => {
                setAllCargo(res.data || []);
                setSelectedDestination("");
                setSelectedCargoId("");
            })
            .catch(err => {
                console.error(err);
                setAllCargo([]);
                setSelectedDestination("");
                setSelectedCargoId("");
            });
    }, [selectedShip]);

    useEffect(() => {
        const socket = new SockJS(`${import.meta.env.VITE_API_BASE_URL}/ws`);

        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000
        });

        client.onConnect = () => {
            console.log("VoyagePage WebSocket connected");

            client.subscribe(`/topic/session/${sessionCode}`, async (message) => {
                const data = JSON.parse(message.body);
                console.log("VOYAGEPAGE WS EVENT:", data);

                if (data.type === "TICK") {
                    await fetchSession();
                    return;
                }

                if (data.type === "VOYAGE_STARTED") {
                    console.log("VOYAGE STARTED EVENT:", data);
                    await fetchSession();
                    await fetchVoyages();
                    return;
                }

                if (data.type === "VOYAGE_FINISHED") {
                    await fetchSession();
                    await fetchVoyages();
                    return;
                }

                if (data.type === "SESSION_PAUSED" || data.type === "SESSION_RUNNING") {
                    await fetchSession();
                    return;
                }
            });
        };

        client.activate();

        return () => {
            client.deactivate();
        };
    }, [sessionCode]);

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

    const handleStartVoyage = async () => {
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

        try {
            await api.post("/voyages/start", {
                shipId: selectedShip.id,
                cargoId: Number(selectedCargoId),
                sessionCode: sessionCode
            });

            await fetchSession();
            await fetchVoyages();

            setStartedVoyageInfo({
                shipName: selectedShip.name,
                origin: selectedShip.currentPort,
                destination: selectedCargo?.destinationPort?.name,
                duration: selectedCargo.requiredTicks,
                price: selectedCargo?.price,
                reward: selectedCargo?.reward
            });

            setShowVoyageStartedPopup(true);
        } catch (err) {
            console.error(err);
            alert(err.response?.data?.message || "Failed to start voyage");
        }
    };

    return (
        <div className="voyage-page">
            <div className="voyage-overlay"></div>

            <div className="voyage-topbar">
                <button
                    className="back-button"
                    onClick={() => navigate(-1)}
                >
                    ← Back
                </button>
            </div>

            <div className="voyage-content">
                <header className="voyage-header">
                    <h1>Voyage Planning</h1>
                    <p>Select a ship, destination and cargo order.</p>
                </header>

                <div className="voyage-cards">
                    <div className="voyage-card">
                        <h2>Ship</h2>

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
                                        disabled={isShipBusy(ship.id)}
                                    >
                                        {ship.name} {isShipBusy(ship.id) ? "🚫 (busy)" : ""}
                                    </option>
                                ))
                            )}
                        </select>

                        {selectedShip && (
                            <div className="voyage-info">
                                <p>Ship: {selectedShip.name}</p>
                                <p>Current Port: {selectedShip?.currentPort || "Unknown"}</p>
                                <p>Capacity: {selectedShip.cargoCapacity}</p>
                                <p>Fuel: {selectedShip.fuelLevel}</p>
                                <p>Condition: {selectedShip.condition}</p>
                            </div>
                        )}
                    </div>

                    <div className="voyage-card">
                        <h2>Destination</h2>

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

                    <div className="voyage-card">
                        <h2>Cargo Orders</h2>

                        {!selectedDestination ? (
                            <p>Select a destination first</p>
                        ) : filteredCargo.length === 0 ? (
                            <p>No cargo orders available for this destination</p>
                        ) : (
                            <>
                                <select
                                    value={selectedCargoId}
                                    onChange={(e) => setSelectedCargoId(e.target.value)}
                                >
                                    <option value="">Select cargo order</option>
                                    {filteredCargo.map(item => (
                                        <option key={item.id} value={item.id}>
                                            Order #{item.id} - {item.originPort?.name} → {item.destinationPort?.name} - Price: {item.price} - Reward: {item.reward}
                                        </option>
                                    ))}
                                </select>

                                <div style={{ marginTop: "15px" }}>
                                    {filteredCargo.map(item => (
                                        <div
                                            key={item.id}
                                            className="cargo-card"
                                            style={{
                                                border: item.id === Number(selectedCargoId)
                                                    ? "2px solid white"
                                                    : undefined,
                                                cursor: "pointer"
                                            }}
                                            onClick={() => setSelectedCargoId(String(item.id))}
                                        >
                                            <p>Order #{item.id}</p>
                                            <p>From: {item.originPort?.name}</p>
                                            <p>To: {item.destinationPort?.name}</p>
                                            <p>Price: {item.price} Talers</p>
                                            <p>Reward: {item.reward} Talers</p>
                                            <p>Required Capacity: {item.requiredCapacity}</p>
                                            <p>Risk: {item.riskLevel}</p>
                                        </div>
                                    ))}
                                </div>
                            </>
                        )}
                    </div>

                    <div className="voyage-card">
                        <h2>Summary</h2>

                        {!selectedShip || !selectedCargo ? (
                            <p>Select ship, destination and cargo order</p>
                        ) : (
                            <div className="voyage-info">
                                <p>Ship: {selectedShip.name}</p>
                                <p>From: {selectedShip?.currentPort || "Unknown"}</p>
                                <p>To: {selectedCargo.destinationPort?.name}</p>
                                <p>Cargo Order: #{selectedCargo.id}</p>
                                <p>Price: {selectedCargo.price} Talers</p>
                                <p>Reward: {selectedCargo.reward} Talers</p>
                                <p>Required Capacity: {selectedCargo.requiredCapacity}</p>
                                <p>Risk: {selectedCargo.riskLevel}</p>
                                <p>Expected Profit: {selectedCargo.reward - selectedCargo.price} Talers</p>
                                {selectedCargo && session && (
                                    <p>
                                        Estimated Duration: {selectedCargo.requiredTicks} days
                                    </p>
                                )}

                                {selectedShip && selectedCargo && selectedShip.cargoCapacity < selectedCargo.requiredCapacity && (
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
                            isShipBusy(selectedShip?.id) ||
                            (selectedShip && selectedCargo && selectedShip.cargoCapacity < selectedCargo.requiredCapacity)
                        }
                    >
                        Start Voyage
                    </button>
                </div>
            </div>

            {showVoyageStartedPopup && (
                <div className="welcome-overlay">
                    <div className="welcome-modal">
                        <h2>Voyage started</h2>
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
                            </>
                        )}

                        <button
                            onClick={() => navigate(`/game/${sessionCode}`)}
                        >
                            Back to Game
                        </button>
                    </div>
                </div>
            )}

        </div>
    );
}