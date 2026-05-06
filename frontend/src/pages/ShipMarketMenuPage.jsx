import { useNavigate, useParams } from "react-router-dom";
import "./ShipMarketPage.css";
import { useContext } from "react";
import { GameContext } from "../layouts/AppLayout";

function ShipMarketMenuPage() {
    const navigate = useNavigate();
    const { sessionCode } = useParams();

    const { session, player } = useContext(GameContext);


    const currentPlayer = session?.players?.find(
        p => p.id === player?.id
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

                <button
                    className="back-button"
                    onClick={() => navigate(`/session/${sessionCode}/game`)}
                >
                    🡸 Back to Game
                </button>

                <header className="market-header">
                    <h1>Ship Market</h1>
                    <p>Select what you want to do in the market.</p>
                </header>

                <div className="ship-cards">

                    <button
                        type="button"
                        className="ship-market-card"
                        onClick={() => navigate(`/session/${sessionCode}/market/buy`)}
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
                        onClick={() => navigate(`/session/${sessionCode}/market/sell`)}
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
                        onClick={() => navigate(`/session/${sessionCode}/market/refuel`)}
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
                        onClick={() => navigate(`/session/${sessionCode}/market/repair`)}
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