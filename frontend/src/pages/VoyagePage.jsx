import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./VoyagePage.css";

export default function VoyagePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [origin, setOrigin] = useState("");
    const [destination, setDestination] = useState("");

    const [ships, setShips] = useState([]);
    const [selectedShip, setSelectedShip] = useState(null);

    const ports = ["Hamburg", "Dubai", "Singapore"];


    useEffect(() => {
        const player = JSON.parse(localStorage.getItem("player"));

        if (!player?.id) return;

        api.get(`/ships/player/${player.id}`)
            .then(res => {
                setShips(res.data);

                if (res.data.length > 0) {
                    setSelectedShip(res.data[0]);
                }
            })
            .catch(err => console.error(err));
    }, []);

    const handleStartVoyage = async () => {
        if (!selectedShip || !origin || !destination) {
            alert("Please select ship and route.");
            return;
        }

        try {
            await api.post("/voyages/start", {
                shipId: selectedShip.id,
                originPort: origin,
                destinationPort: destination,
            });

            navigate(`/game/${sessionCode}`);
        } catch (err) {
            console.error(err);
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
                    <p>Select a ship and define your route.</p>
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
                                setSelectedShip(ship);
                            }}
                        >
                            {ships.length === 0 ? (
                                <option disabled>No ships available</option>
                            ) : (
                                ships.map(ship => (
                                    <option key={ship.id} value={ship.id}>
                                        {ship.name}
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
                        <h2>Route</h2>

                        <div className="voyage-row">

                            <div className="voyage-field">
                                <label>Origin</label>
                                <select
                                    value={origin}
                                    onChange={(e) => setOrigin(e.target.value)}
                                >
                                    <option value="">Select</option>
                                    {ports.map(p => (
                                        <option key={p}>{p}</option>
                                    ))}
                                </select>
                            </div>

                            <div className="voyage-field">
                                <label>Destination</label>
                                <select
                                    value={destination}
                                    onChange={(e) => setDestination(e.target.value)}
                                >
                                    <option value="">Select</option>
                                    {ports.map(p => (
                                        <option key={p}>{p}</option>
                                    ))}
                                </select>
                            </div>

                        </div>
                    </div>

                </div>

                <div className="voyage-action-panel">
                    <div>
                        <h3>Route</h3>
                        <p className="route-text">
                            {origin && destination ? (
                                <>
                                    <span>{origin}</span>
                                    <span className="arrow">→</span>
                                    <span>{destination}</span>
                                </>
                            ) : "No route selected"}
                        </p>
                    </div>

                    <button
                        className="voyage-start-btn"
                        onClick={handleStartVoyage}
                    >
                        Start Voyage
                    </button>
                </div>

            </div>
        </div>
    );
}