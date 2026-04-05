import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./VoyagePage.css";


export default function VoyagePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [ships, setShips] = useState([]);
    const [selectedShip, setSelectedShip] = useState(null);

    const [goods, setGoods] = useState([]);
    const [cargo, setCargo] = useState([]);

    const [origin, setOrigin] = useState("");
    const [destination, setDestination] = useState("");

    const [voyages, setVoyages] = useState([]);
    const [ports, setPorts] = useState([]);

    const refreshData = () => {
        if (selectedShip) {
            api.get(`/cargo?portName=${origin}`)
                .then(res => setCargo(res.data));
        }

        if (!origin) return;

        const portObj = ports.find(
            p => p.name.toLowerCase().trim() === origin.toLowerCase().trim()
        );

        if (!portObj) return;

        api.get(`/port-goods/${portObj.id}`)
            .then(res => setGoods(res.data));
    };

    const sellCargo = async (item) => {
        try {
            await api.post("/cargo/sell", {
                shipId: selectedShip.id,
                goodId: item.goodId,
                quantity: item.quantity
            });

            alert("Sold 💰");
        } catch (err) {
            console.error(err);
            alert("Failed to sell");
        }
        refreshData();
    };

    const handleLoadCargo = async (good) => {
        const player = JSON.parse(localStorage.getItem("player"));

        try {
            const portObj = ports.find(
                p => p.name.toLowerCase().trim() === origin.toLowerCase().trim()
            );

            console.log("ORIGIN:", origin);
            console.log("PORT OBJ:", portObj);
            if (!portObj) return;

            await api.post("/cargo/load", {
                playerId: player.id,
                shipId: selectedShip.id,
                portId: portObj.id,
                goodId: good.goodId,
                quantity: good.quantity || 1
            });
            refreshData();

            alert("Loaded 🚢");

        } catch (err) {
            console.error(err);
            alert("Failed to load cargo");
        }
    };


    useEffect(() => {
        const player = JSON.parse(localStorage.getItem("player"));
        const port = localStorage.getItem(`currentPort-${sessionCode}`);

        if (port) {
            setOrigin(port);
        } else {
            alert("Please select a port on the map first.");
            navigate(`/game/${sessionCode}`);
        }

        if (!player?.id) return;

        api.get(`/ships/player/${player.id}`)
            .then(res => {
                setShips(res.data);

                if (res.data.length > 0) {
                    setSelectedShip(res.data[0]);
                }
            })
            .catch(err => console.error(err));

        api.get("/voyages")
            .then(res => setVoyages(res.data))
            .catch(err => console.error(err));

        api.get("/ports")
            .then(res => setPorts(res.data))
            .catch(err => console.error(err));

    }, []);


    useEffect(() => {
        if (!origin || ports.length === 0) return;

        const portObj = ports.find(
            p => p.name.toLowerCase().trim() === origin.toLowerCase().trim()
        );

        console.log("ORIGIN:", origin);
        console.log("PORT OBJ:", portObj);

        if (!portObj || !portObj.id) return;

        api.get(`/port-goods/${portObj.id}`)
            .then(res => {
                console.log("GOODS:", res.data);
                setGoods(res.data);
            })
            .catch(err => console.error(err));

    }, [origin, ports]);

    useEffect(() => {
        if (!selectedShip || !origin) return;

        api.get(`/cargo?portName=${origin}`)
            .then(res => {
                console.log("CARGO:", res.data);
                setCargo(res.data);
            })
            .catch(err => console.error(err));
    }, [selectedShip, origin]);

    const handleStartVoyage = async () => {
        if (!selectedShip) {
            alert("Select a ship");
            return;
        }

        if (!destination) {
            alert("Select a destination");
            return;
        }

        try {
            const originObj = ports.find(p => p.name === origin);

            await api.post("/voyages/start", {
                shipId: selectedShip.id,
                originPort: originObj.id,
                destinationPort: destination
            });

            navigate(`/game/${sessionCode}`);
        } catch (err) {
            console.error(err);
            alert("Failed to start voyage");
        }
    };


    const isShipBusy = (shipId) => {
        return voyages.some(
            v => v.shipId === shipId && v.status === "RUNNING"
        );
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
                    <p>Select a ship and choose a cargo.</p>
                </header>

                <p style={{ opacity: 0.7 }}>
                    📍 Current Port: {origin}
                </p>

                <div className="voyage-cards">

                    <h2>Destination</h2>

                    <select
                        value={destination}
                        onChange={(e) => setDestination(e.target.value)}
                    >
                        <option value="">Select destination</option>

                        {ports
                            .filter(p => p.name !== origin)
                            .sort((a, b) => a.name.localeCompare(b.name))
                            .map(p => (
                                <option key={p.id} value={p.id}>
                                    {p.name}
                                </option>
                            ))}
                    </select>

                    {destination && (
                        <p className="voyage-info">
                            Selected: {ports.find(p => p.id == destination)?.name}
                        </p>
                    )}


                    <div className="voyage-card">
                        <h2>Ship</h2>

                        <select
                            value={selectedShip?.id || ""}
                            onChange={(e) => {
                                const ship = ships.find(
                                    s => s.id === Number(e.target.value)
                                );
                                setSelectedShip(ship);
                            }}
                        >
                            {ships.length === 0 ? (
                                <option disabled>No ships available</option>
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
                            <p className="voyage-info">
                                Selected: {selectedShip.name}
                            </p>
                        )}
                    </div>


                    <div className="voyage-card">
                        <h2>Available Goods</h2>

                        {goods.length === 0 ? (
                            <p>No goods available</p>
                        ) : (
                            goods.map(good => (
                                <div key={good.goodId} className="cargo-card">
                                    <p><strong>{good.name}</strong></p>
                                    <p>💰 Buy: {good.buyPrice}</p>
                                    <p>💸 Sell: {good.sellPrice}</p>
                                    <p>📦 Stock: {good.stock}</p>

                                    <input
                                        type="number"
                                        min="1"
                                        placeholder="Qty"
                                        onChange={(e) => {
                                            const value = Number(e.target.value);
                                            setGoods(prev =>
                                                prev.map(g =>
                                                    g.goodId === good.goodId
                                                        ? { ...g, quantity: value }
                                                        : g
                                                )
                                            );
                                        }}
                                    />

                                    <button onClick={() => handleLoadCargo(good)}>
                                        Load
                                    </button>
                                </div>
                            ))
                        )}
                    </div>

                    <div className="voyage-card">
                        <h2>Your Cargo</h2>

                        {cargo.length === 0 ? (
                            <p>No cargo loaded</p>
                        ) : (
                            cargo.map(item => (
                                <div key={item.goodId} className="cargo-card">
                                    <p><strong>{item.name}</strong></p>
                                    <p>📦 Qty: {item.quantity}</p>

                                    <button onClick={() => sellCargo(item)}>
                                        Sell
                                    </button>
                                </div>
                            ))
                        )}
                    </div>

                </div>

                <div style={{ marginTop: "30px", textAlign: "center" }}>
                    <button
                        className="start-button"
                        onClick={handleStartVoyage}
                        disabled={isShipBusy(selectedShip?.id)}
                    >
                        🚀 Start Voyage
                    </button>
                </div>

            </div>
        </div>
    );
}