import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import "./ShipRefuelPage.css";

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

    const [toastType, setToastType] = useState("success");

    const [fuelAmount, setFuelAmount] = useState(0);

    const [refuelCost, setRefuelCost] = useState(0);

    const VAT = 0.20;

    const netCost = refuelCost / (1 + VAT);
    const vatAmount = refuelCost - netCost;

    const handleRefuel = async () => {
        try {
            await api.post(`/ships/${selectedShip.id}/refuel`, {
                fuelAmount: fuelAmount
            });

            setToast("Ship refueled ⛽");
            setToastType("success");

            const res = await api.get(`/sessions/${sessionCode}`);
            setSession(res.data);
            setSelectedShip(null);
            setFuelAmount(0);
            window.scrollTo(0, 0);

        } catch (err) {
            setToast(err.response?.data?.message || "Refuel failed");
            setToastType("error");
        }

        setTimeout(() => setToast(""), 2000);
    };

    return (
        <div className="market-page">
            <div className="market-content">

                <button className="back-button" onClick={() => navigate(`/market/${sessionCode}`)}>
                    ← Back
                </button>

                <header className="market-header">
                    <h1>Refuel Ship</h1>
                    <p>Select a ship to refuel.</p>
                </header>

                {toast && (
                    <div className={`toast-notification ${toastType === "error" ? "toast-error" : ""}`}>
                        {toast}
                    </div>
                )}

                <div className="refuel-container">
                    {ships.map((ship) => (
                        <div
                            key={ship.id}
                            className={`refuel-card ${selectedShip?.id === ship.id ? "active" : ""}`}
                            onClick={() => {
                                if (selectedShip?.id !== ship.id) {
                                    setSelectedShip(ship);
                                    setFuelAmount(0);
                                    setRefuelCost(0);
                                }
                            }}
                        >
                            <div className="refuel-row">

                                {/* LEFT */}
                                <div className="refuel-left">

                                    <div className="refuel-name">
                                        {ship.name}
                                    </div>

                                    <div className="refuel-info-row">
                                        <span className="refuel-fuel">
                                            ⛽ {Math.round(ship.fuelLevel ?? 0)}%
                                        </span>

                                    </div>

                                    <div className="fuel-bar">
                                        <div
                                            className="fuel-fill"
                                            style={{
                                                width: `${ship.fuelLevel ?? 0}%`,
                                                background: (() => {
                                                    const fuel = ship.fuelLevel ?? 0;

                                                    if (fuel <= 20) return "#ef4444";
                                                    if (fuel <= 50) return "#f59e0b";
                                                    return "#22c55e";
                                                })()
                                            }}
                                        />
                                    </div>

                                    {selectedShip?.id === ship.id && (
                                        <div className="refuel-bottom">

                                            <div className="refuel-section-title">
                                                Refuel
                                            </div>
                                            <div className="refuel-subtitle">
                                                Adjust how much fuel you want to buy
                                            </div>

                                            <div className="refuel-box">

                                                <div className="refuel-box-title">Refuel Summary</div>

                                                <div className="refuel-info-grid">

                                                    <div>Fuel to add</div>
                                                    <div>+{fuelAmount}</div>

                                                    <div>After refuel</div>
                                                    <div>{Math.min(100, (ship.fuelLevel ?? 0) + fuelAmount)}%</div>

                                                    <div>Net</div>
                                                    <div>${Math.round(netCost)}</div>

                                                    <div>VAT (20%)</div>
                                                    <div>${Math.round(vatAmount)}</div>

                                                    <div className="refuel-divider"></div>

                                                    <div className="refuel-total-row">
                                                        <span>Total</span>
                                                        <span className="refuel-total-value">
                                                            ${Math.round(refuelCost)}
                                                        </span>
                                                    </div>

                                                </div>

                                                {currentPlayer?.balance < refuelCost && (
                                                    <div className="refuel-warning">
                                                        ⚠ Not enough balance
                                                    </div>
                                                )}

                                            </div>

                                            <input
                                                type="range"
                                                min="0"
                                                max={Math.max(0, 100 - (ship.fuelLevel ?? 0))}
                                                value={fuelAmount}

                                                onClick={(e) => e.stopPropagation()}

                                                onChange={async (e) => {
                                                    const maxFuel = Math.max(0, 100 - (ship.fuelLevel ?? 0));
                                                    const value = Math.min(maxFuel, Math.max(0, Number(e.target.value)));

                                                    setFuelAmount(value);

                                                    try {
                                                        const res = await api.get(
                                                            `/ships/${ship.id}/refuel-cost?fuelAmount=${value}`
                                                        );
                                                        setRefuelCost(res.data);
                                                    } catch {
                                                        setRefuelCost(0);
                                                    }
                                                }}
                                                className="refuel-slider"
                                            />

                                            <div className="refuel-actions-row">

                                                <button
                                                    className="refuel-max-btn"
                                                    onClick={async (e) => {
                                                        e.stopPropagation();
                                                        const maxFuel = Math.max(0, 100 - (ship.fuelLevel ?? 0));
                                                        setFuelAmount(maxFuel);

                                                        try {
                                                            const res = await api.get(
                                                                `/ships/${ship.id}/refuel-cost?fuelAmount=${maxFuel}`
                                                            );
                                                            setRefuelCost(res.data);
                                                        } catch {
                                                            setRefuelCost(0);
                                                        }
                                                    }}
                                                >
                                                    MAX
                                                </button>

                                                <button
                                                    className="refuel-main-btn"
                                                    onClick={(e) => {
                                                        e.stopPropagation();
                                                        handleRefuel();
                                                    }}
                                                    disabled={!fuelAmount || currentPlayer?.balance < refuelCost}
                                                >
                                                    ⛽ Buy Fuel – ${Math.round(refuelCost)}
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

export default ShipRefuelPage;