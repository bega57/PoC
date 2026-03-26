import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import cheapSide from "../assets/ships/cheapSide.png";
import middleSide from "../assets/ships/middleSide.png";
import expensiveSide from "../assets/ships/expensiveSide.png";
import "./CompanyPage.css";

function CompanyPage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [session, setSession] = useState(null);

    const storedPlayer = JSON.parse(localStorage.getItem("player"));

    useEffect(() => {
        const fetchSession = async () => {
            try {
                const response = await api.get(`/sessions/${sessionCode}`);
                setSession(response.data);
            } catch (error) {
                console.error("Failed to fetch company data:", error);
            }
        };

        fetchSession();
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
                                        <p>Type: {getShipDisplayName(ship.type)}</p>
                                        <p>Condition: {ship.condition}%</p>
                                        <p>Fuel: {ship.fuelLevel}%</p>
                                        <p>Capacity: {ship.capacity}</p>
                                        <p>Speed: {ship.speed}</p>
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