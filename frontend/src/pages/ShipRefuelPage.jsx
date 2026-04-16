import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import "./ShipMarketPage.css";

function ShipRefuelPage() {
    const navigate = useNavigate();
    const { sessionCode } = useParams();

    const [session, setSession] = useState(null);
    const [selectedShip, setSelectedShip] = useState(null);
    const [toast, setToast] = useState("");

    useEffect(() => {
        const fetchSession = async () => {
            const res = await api.get(`/sessions/${sessionCode}`);
            setSession(res.data);
        };
        fetchSession();
    }, [sessionCode]);

    const activePlayerId = Number(sessionStorage.getItem(`activePlayerId-${sessionCode}`));

    const currentPlayer = session?.players?.find(p => p.id === activePlayerId);

    const ships = currentPlayer?.ships || [];

    const handleRefuel = () => {
        setToast("Refuel functionality coming soon ⛽");
        setTimeout(() => setToast(""), 2000);
    };

    return (
        <div className="market-page">
            <div className="market-content">

                <button className="back-button" onClick={() => navigate(`/market/${sessionCode}`)}>
                    ← Back
                </button>

                <header className="market-header">
                    <h1>Refuel Ships</h1>
                    <p>Select a ship to refuel.</p>
                </header>

                {toast && <div className="toast-notification">{toast}</div>}

                <div className="ship-cards">
                    {ships.map((ship) => (
                        <button
                            key={ship.id}
                            className={`ship-market-card ${selectedShip?.id === ship.id ? "active" : ""}`}
                            onClick={() => setSelectedShip(ship)}
                        >
                            <div className="ship-card-right">
                                <div className="ship-main-info">
                                    <div className="ship-title-row">
                                        <h2>{ship.name}</h2>
                                    </div>
                                    <p className="ship-description fuel">
                                        Fuel: {ship.fuel ?? "N/A"}
                                    </p>
                                </div>
                            </div>
                        </button>
                    ))}
                </div>

                {selectedShip && (
                    <div className="buy-panel">
                        <p>Selected: {selectedShip.name}</p>
                        <button className="buy-button" onClick={handleRefuel}>
                            Refuel
                        </button>
                    </div>
                )}

            </div>
        </div>
    );
}

export default ShipRefuelPage;