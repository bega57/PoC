import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import cheapSide from "../assets/ships/cheapSide.png";
import middleSide from "../assets/ships/middleSide.png";
import expensiveSide from "../assets/ships/expensiveSide.png";
import "./ShipMarketPage.css";

function SellShipPage() {
    const navigate = useNavigate();
    const { sessionCode } = useParams();

    const [session, setSession] = useState(null);
    const [selectedShip, setSelectedShip] = useState(null);

    const [showSellModal, setShowSellModal] = useState(false);
    const [message, setMessage] = useState("");
    const [toastMessage, setToastMessage] = useState("");
    const [isSelling, setIsSelling] = useState(false);

    useEffect(() => {
        const fetchSession = async () => {
            try {
                const response = await api.get(`/sessions/${sessionCode}`);
                setSession(response.data);
            } catch (error) {
                console.error(error);
            }
        };

        fetchSession();
    }, [sessionCode]);

    const activePlayerId = Number(localStorage.getItem("activePlayerId"));

    const currentPlayer = session?.players?.find(
        (player) => player.id === activePlayerId
    );

    const playerShips = currentPlayer?.ships || [];

    // default selection
    useEffect(() => {
        if (playerShips.length > 0 && !selectedShip) {
            setSelectedShip(playerShips[0]);
        }
    }, [playerShips, selectedShip]);

    const showToast = (text) => {
        setToastMessage(text);
        setTimeout(() => setToastMessage(""), 2500);
    };

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

    const getSellPrice = (ship) => {
        const basePrices = {
            CHEAP: 1000,
            MEDIUM: 2500,
            EXPENSIVE: 4000
        };

        return Math.floor(basePrices[ship.type] * (ship.condition / 100));
    };

    const handleSellShip = async () => {
        try {
            setIsSelling(true);
            setMessage("");

            await api.post("/ships/sell", {
                playerId: currentPlayer.id,
                shipId: selectedShip.id
            });

            const refreshed = await api.get(`/sessions/${sessionCode}`);
            setSession(refreshed.data);

            setShowSellModal(false);
            setSelectedShip(null);

            showToast(`Sold ${selectedShip.name} for $${getSellPrice(selectedShip)}`);
        } catch (error) {
            console.error(error);
            setMessage("Failed to sell ship.");
        } finally {
            setIsSelling(false);
        }
    };

    if (!session) {
        return <div style={{ color: "white", padding: "20px" }}>Loading market...</div>;
    }

    if (!currentPlayer) {
        return (
            <div style={{ color: "white", padding: "20px" }}>
                No active player found for this session.
            </div>
        );
    }

    return (
        <div className="market-page">
            <div className="market-overlay"></div>

            {toastMessage && <div className="toast-notification">{toastMessage}</div>}

            <div className="market-content">
                <div className="market-topbar">
                    <button
                        className="back-button"
                        onClick={() => navigate(`/market/${sessionCode}`)}
                    >
                        ← Back
                    </button>
                </div>

                <header className="market-header">
                    <h1>Sell Ship</h1>
                    <p>Select one of your ships to sell.</p>
                </header>

                <div className="player-balance-bar">
                    <span>
                        Player: {currentPlayer.username}
                        {currentPlayer.companyName
                            ? ` | Company: ${currentPlayer.companyName}`
                            : ""}
                    </span>
                    <span>Balance: ${currentPlayer.balance}</span>
                </div>

                {playerShips.length === 0 ? (
                    <p style={{ color: "white", marginTop: "20px" }}>
                        You don't own any ships.
                    </p>
                ) : (
                    <div className="ship-cards">
                        {playerShips.map((ship) => (
                            <button
                                key={ship.id}
                                className={`ship-market-card ${selectedShip?.id === ship.id ? "active" : ""}`}
                                onClick={() => setSelectedShip(ship)}
                            >
                                <div className="ship-card-left">
                                    <div className="ship-image-box">
                                        <img src={getShipImage(ship.type)} alt={ship.name} />
                                    </div>
                                </div>

                                <div className="ship-card-right">
                                    <div className="ship-main-info">
                                        <div className="ship-title-row">
                                            <h2>{ship.name}</h2>
                                            <span className="ship-price">
                                                ${getSellPrice(ship)}
                                            </span>
                                        </div>

                                        <p className="ship-description">
                                            {getShipDisplayName(ship.type)}
                                        </p>
                                    </div>

                                    <div className="ship-stats">
                                        <div className="stat-block">
                                            <span className="stat-label">Condition</span>
                                            <span className="stat-value">{ship.condition}%</span>
                                        </div>

                                        <div className="stat-block">
                                            <span className="stat-label">Fuel</span>
                                            <span className="stat-value">{ship.fuelLevel}%</span>
                                        </div>
                                    </div>
                                </div>
                            </button>
                        ))}
                    </div>
                )}

                {selectedShip && (
                    <div className="buy-panel">
                        <div className="buy-panel-text">
                            <h3>Selected Ship</h3>
                            <p>
                                {selectedShip.name} — ${getSellPrice(selectedShip)}
                            </p>
                        </div>

                        <button
                            className="buy-button"
                            onClick={() => setShowSellModal(true)}
                        >
                            Sell {selectedShip.name}
                        </button>
                    </div>
                )}
            </div>

            {showSellModal && selectedShip && (
                <div className="modal-overlay">
                    <div className="buy-modal">
                        <h2>Sell {selectedShip.name}</h2>

                        <p className="modal-price">
                            You will receive: ${getSellPrice(selectedShip)}
                        </p>

                        {message && <p className="modal-message">{message}</p>}

                        <div className="modal-actions">
                            <button
                                className="secondary-button"
                                onClick={() => setShowSellModal(false)}
                                disabled={isSelling}
                            >
                                Cancel
                            </button>

                            <button
                                className="buy-button"
                                onClick={handleSellShip}
                                disabled={isSelling}
                            >
                                {isSelling ? "Selling..." : "Confirm Sale"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default SellShipPage;