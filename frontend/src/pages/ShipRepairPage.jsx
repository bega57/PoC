import { useState, useContext, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import "./ShipRepairPage.css";
import { GameContext } from "../layouts/AppLayout";

function ShipRepairPage() {
    const navigate = useNavigate();
    const { sessionCode } = useParams();

    const { session, player } = useContext(GameContext);

    const [selectedShip, setSelectedShip] = useState(null);
    const [toast, setToast] = useState("");

    const currentPlayer = session?.players?.find(
        p => p.id === player?.id
    );

    const [repairAmount, setRepairAmount] = useState(0);
    const [repairCost, setRepairCost] = useState(0);

    const VAT = 0.20;

    const netCost = repairCost / (1 + VAT);
    const vatAmount = repairCost - netCost;

    const [ships, setShips] = useState([]);

    useEffect(() => {
        if (!player?.id) return;

        api.get(`/ships/player/${player.id}`)
            .then(res => setShips(res.data))
            .catch(err => console.error("Failed to load ships:", err));
    }, [player?.id]);

    const handleRepair = async () => {
        try {
            await api.post(`/ships/${selectedShip.id}/repair`, {
                repairAmount: repairAmount,
                sessionCode: sessionCode
            });

            setToast("Ship repaired 🔧");

            setShips(prev =>
                prev.map(ship => {
                    if (ship.id === selectedShip.id) {
                        return {
                            ...ship,
                            condition: Math.min(100, (ship.condition ?? 0) + repairAmount)
                        };
                    }

                    return ship;
                })
            );

            setSelectedShip(null);
            setRepairAmount(0);

        } catch (err) {
            setToast(err.response?.data?.message || "Repair failed");
        }

        setTimeout(() => setToast(""), 2000);
    };

    return (
        <div className="market-page">
            <div className="market-content">

                <button className="back-button" onClick={() => navigate(`/session/${sessionCode}/market`)}>
                    🡸 Return to Market
                </button>

                <header className="market-header">
                    <h1>Repair Ship</h1>
                    <p>Select a ship to repair.</p>
                </header>

                {toast && <div className="toast-notification">{toast}</div>}

                <div className="repair-container">
                    {ships.map((ship) => (
                        <div
                            key={ship.id}
                            className={`repair-card ${selectedShip?.id === ship.id ? "active" : ""}`}
                            onClick={async () => {
                                if (selectedShip?.id !== ship.id) {
                                    setSelectedShip(ship);
                                    setRepairAmount(0);
                                    setRepairCost(0);
                                }
                            }}
                        >
                            <div className="repair-row">

                                <div className="repair-left">

                                    <div className="repair-name">
                                        {ship.name}
                                    </div>

                                    <div className="repair-fuel">
                                        🔧 {ship.condition ?? 0}%
                                    </div>

                                    <div className="fuel-bar">
                                        <div
                                            className="fuel-fill"
                                            style={{
                                                width: `${ship.condition ?? 0}%`,
                                                background: (() => {
                                                    const c = ship.condition ?? 0;
                                                    if (c <= 20) return "#ef4444";
                                                    if (c <= 50) return "#f59e0b";
                                                    return "#22c55e";
                                                })()
                                            }}
                                        />
                                    </div>

                                    {selectedShip?.id === ship.id && (
                                        <div className="repair-bottom">

                                            <div className="repair-section-title">
                                                Repair
                                            </div>
                                            <div className="repair-subtitle">
                                                Adjust how much condition you want to repair
                                            </div>

                                            <div className="repair-box">

                                                <div className="repair-box-title">
                                                    Repair Summary
                                                </div>

                                                <div className="repair-info-grid">

                                                    <div>Repair</div>
                                                    <div>+{repairAmount}</div>

                                                    <div>After</div>
                                                    <div>
                                                        {Math.min(100, (ship.condition ?? 0) + repairAmount)}%
                                                    </div>

                                                    <div>Net</div>
                                                    <div>${Math.round(netCost)}</div>

                                                    <div>VAT (20%)</div>
                                                    <div>${Math.round(vatAmount)}</div>

                                                    <div className="repair-divider"></div>

                                                    <div className="repair-total-row">
                                                        <span>Total</span>
                                                        <span className="repair-total-value">
                                                            ${Math.round(repairCost)}
                                                        </span>
                                                    </div>

                                                </div>

                                                {currentPlayer?.balance < repairCost && (
                                                    <div className="repair-warning">
                                                        ⚠ Not enough balance
                                                    </div>
                                                )}

                                            </div>

                                            <input
                                                type="range"
                                                min="0"
                                                max={Math.max(0, 100 - (ship.condition ?? 0))}
                                                value={repairAmount}
                                                onClick={(e) => e.stopPropagation()}
                                                onChange={async (e) => {
                                                    const max = Math.max(0, 100 - (ship.condition ?? 0));
                                                    const value = Math.min(max, Math.max(0, Number(e.target.value)));

                                                    setRepairAmount(value);

                                                    try {
                                                        const res = await api.get(
                                                            `/ships/${ship.id}/repair-cost?repairAmount=${value}`
                                                        );
                                                        setRepairCost(res.data);
                                                    } catch {
                                                        setRepairCost(0);
                                                    }
                                                }}
                                                className="repair-slider"
                                            />

                                            <div className="repair-actions-row">

                                                <button
                                                    className="repair-max-btn"
                                                    onClick={async (e) => {
                                                        e.stopPropagation();

                                                        const max = Math.max(0, 100 - (ship.condition ?? 0));
                                                        setRepairAmount(max);

                                                        try {
                                                            const res = await api.get(
                                                                `/ships/${ship.id}/repair-cost?repairAmount=${max}`
                                                            );
                                                            setRepairCost(res.data);
                                                        } catch {
                                                            setRepairCost(0);
                                                        }
                                                    }}
                                                >
                                                    MAX
                                                </button>

                                                <button
                                                    className="repair-main-btn"
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        handleRepair();
                                                    }}
                                                    disabled={!repairAmount || currentPlayer?.balance < repairCost}
                                                >
                                                    🔧 Repair – ${Math.round(repairCost)}
                                                </button>

                                            </div>

                                        </div>
                                    )}

                                </div>
                            </div>
                        </div>
                    ))}
                </div>

            </div>
        </div>
    );
}

export default ShipRepairPage;