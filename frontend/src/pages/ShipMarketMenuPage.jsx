import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import "./ShipMarketPage.css";

function ShipMarketMenuPage() {
    const navigate = useNavigate();
    const { sessionCode } = useParams();

    const [session, setSession] = useState(null);

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

    const activePlayerId = Number(sessionStorage.getItem(`activePlayerId-${sessionCode}`));

    const currentPlayer = session?.players?.find(
        (player) => player.id === activePlayerId
    );

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

            <div className="market-content">
                <div className="market-topbar">
                    <button
                        className="back-button"
                        onClick={() => navigate(`/game/${sessionCode}`)}
                    >
                        ← Back
                    </button>
                </div>

                <header className="market-header">
                    <h1>Ship Market</h1>
                    <p>Select what you want to do in the market.</p>
                </header>

                <div className="player-balance-bar">
                    <span>
                        Player: {currentPlayer.username}
                        {currentPlayer.companyName
                            ? ` | Company name: ${currentPlayer.companyName}`
                            : ""}
                    </span>
                    <span>Balance: ${currentPlayer.balance}</span>
                </div>

                <div className="ship-cards">
                    <button
                        type="button"
                        className="ship-market-card"
                        onClick={() => navigate(`/market/${sessionCode}/buy`)}
                    >
                        <div className="ship-card-right">
                            <div className="ship-main-info">
                                <div className="ship-title-row">
                                    <h2>Buy Ship</h2>
                                </div>
                                <p className="ship-description">
                                    Choose a ship and expand your fleet with new or pre-owned vessels.
                                </p>
                            </div>
                        </div>
                    </button>

                    <button
                        type="button"
                        className="ship-market-card"
                        onClick={() => navigate(`/market/${sessionCode}/sell`)}
                    >
                        <div className="ship-card-right">
                            <div className="ship-main-info">
                                <div className="ship-title-row">
                                    <h2>Sell Ship</h2>
                                </div>
                                <p className="ship-description">
                                    Sell one of your owned ships.
                                </p>
                            </div>
                        </div>
                    </button>

                    <button
                        type="button"
                        className="ship-market-card"
                        onClick={() => navigate(`/market/${sessionCode}/refuel`)}
                    >
                        <div className="ship-card-right">
                            <div className="ship-main-info">
                                <div className="ship-title-row">
                                    <h2>Refuel Ship</h2>
                                </div>
                                <p className="ship-description">
                                    Refill your ship's fuel to continue traveling.
                                </p>
                            </div>
                        </div>
                    </button>

                    <button
                        type="button"
                        className="ship-market-card"
                        onClick={() => navigate(`/market/${sessionCode}/repair`)}
                    >
                        <div className="ship-card-right">
                            <div className="ship-main-info">
                                <div className="ship-title-row">
                                    <h2>Repair Ship</h2>
                                </div>
                                <p className="ship-description">
                                    Fix damaged ships and restore performance.
                                </p>
                            </div>
                        </div>
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ShipMarketMenuPage;