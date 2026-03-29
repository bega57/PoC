import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import "./VoyagePage.css";

export default function VoyagePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [origin, setOrigin] = useState("");

    const [ships, setShips] = useState([]);
    const [selectedShip, setSelectedShip] = useState(null);

    const [cargoList, setCargoList] = useState([]);
    const [selectedCargo, setSelectedCargo] = useState(null);


    useEffect(() => {
        const player = JSON.parse(localStorage.getItem("player"));
        const port = localStorage.getItem("currentPort");

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
    }, []);


    useEffect(() => {
        if (!origin) return;

        fetch(`http://localhost:8080/cargo?portName=${origin}`)
            .then(res => res.json())
            .then(data => {
                console.log("CARGO:", data);
                setCargoList(data);
            })
            .catch(err => console.error("Cargo error:", err));
    }, [origin]);


    const handleStartVoyage = async () => {
        if (!selectedShip || !selectedCargo) {
            alert("Please select a ship and cargo.");
            return;
        }

        try {
            await api.post("/voyages/start", {
                shipId: selectedShip.id,
                cargoId: selectedCargo.id
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
                    <p>Select a ship and choose a cargo.</p>
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
                        <h2>Available Cargo</h2>

                        {cargoList.length === 0 ? (
                            <p>No cargo available</p>
                        ) : (
                            cargoList.map(cargo => (
                                <div key={cargo.id} className="cargo-card">
                                    <p>
                                        {cargo.originPort.name} → {cargo.destinationPort.name}
                                    </p>
                                    <p> {cargo.price}€</p>

                                    <button onClick={() => setSelectedCargo(cargo)}>
                                        Accept
                                    </button>
                                </div>
                            ))
                        )}
                    </div>

                </div>


                {selectedCargo && (
                    <div className="voyage-action-panel">
                        <div>
                            <h3>Selected Route</h3>
                            <p className="route-text">
                                <span>{selectedCargo.originPort.name}</span>
                                <span className="arrow">→</span>
                                <span>{selectedCargo.destinationPort.name}</span>
                            </p>
                        </div>

                        <button
                            className="voyage-start-btn"
                            onClick={handleStartVoyage}
                            disabled={!selectedCargo || selectedShip?.traveling}
                        >
                            Start Voyage
                        </button>

                        {selectedShip?.traveling && (
                            <p style={{ color: "orange", marginTop: "10px" }}>
                                🚢 Ship is already traveling
                            </p>
                        )}
                    </div>
                )}

            </div>
        </div>
    );
}