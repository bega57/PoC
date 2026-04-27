import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./CargoOffersPage.css";

function CargoOffersPage() {
    const [offers, setOffers] = useState([]);
    const [fromPort, setFromPort] = useState("");
    const [sortBy, setSortBy] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        fetchOffers();
    }, []);

    const fetchOffers = async () => {
        try {
            const res = await axios.get(
                `${import.meta.env.VITE_API_BASE_URL}/cargo/offers`
            );
            setOffers(res.data);
        } catch (err) {
            console.error("Error fetching cargo offers", err);
        }
    };

    const portOptions = useMemo(() => {
        const ports = [
            ...new Set(
                offers
                    .map((offer) => offer.fromPort)
                    .filter(Boolean)
            )
        ];
        return ports.sort((a, b) => a.localeCompare(b));
    }, [offers]);

    const getRiskLevelValue = (riskLevel) => {
        const riskMap = { LOW: 0, MEDIUM: 1, HIGH: 2 };
        return riskMap[riskLevel] ?? 999;
    };

    const filteredOffers = useMemo(() => {
        let data = [...offers];

        if (fromPort) {
            data = data.filter((offer) => offer.fromPort === fromPort);        }

        if (sortBy === "reward") {
            data.sort((a, b) => b.reward - a.reward);
        }

        if (sortBy === "risk") {
            data.sort((a, b) => getRiskLevelValue(a.riskLevel) - getRiskLevelValue(b.riskLevel));
        }

        return data;
    }, [offers, fromPort, sortBy]);

    return (
        <div className="cargo-page">
            <div className="cargo-overlay"></div>

            <div className="cargo-content">
                <div className="cargo-topbar">
                    <button
                        className="back-button"
                        onClick={() => navigate(-1)}
                    >
                        ← Back
                    </button>
                </div>

                <div className="cargo-header">
                    <h1>Cargo Offers</h1>
                </div>

                <div className="cargo-toolbar">
                    <div></div>

                    <div className="cargo-filter-bar">
                        <select
                            value={fromPort}
                            onChange={(e) => setFromPort(e.target.value)}
                            className="cargo-filter"
                        >
                            <option value="">All Ports</option>
                            {portOptions.map((port) => (
                                <option key={port} value={port}>
                                    {port}
                                </option>
                            ))}
                        </select>

                        <select
                            value={sortBy}
                            onChange={(e) => setSortBy(e.target.value)}
                            className="cargo-filter"
                        >
                            <option value="">Default Sort</option>
                            <option value="reward">Highest Reward</option>
                            <option value="risk">Lowest Risk</option>
                        </select>
                    </div>
                </div>

                <div className="cargo-cards">
                    {filteredOffers.map((offer) => (
                        <div className="cargo-market-card" key={offer.id}>
                            <div className="cargo-card-left">
                                <div className="cargo-port-box">
                                    <span className="cargo-port-label">FROM PORT</span>
                                    <h2>{offer.fromPort  || "Unknown"}</h2>
                                    <p className="cargo-destination">
                                        To: {offer.toPort || "Unknown"}
                                    </p>
                                </div>
                            </div>

                            <div className="cargo-card-right">
                                <div className="cargo-title-row">
                                    <h2>{offer.name}</h2>
                                    <div className="cargo-reward">
                                        Profit: {offer.reward - Math.round(offer.price * 1.2)} $
                                    </div>
                                </div>

                                <div className="cargo-stats">
                                    <div className="cargo-stat-block">
                                        <span className="cargo-stat-label">Price</span>
                                        <span className="cargo-stat-value">
                                            ${Math.round(offer.price * 1.2)}
                                            <span style={{ fontSize: "11px", opacity: 0.7 }}> incl. VAT</span>
                                        </span>
                                    </div>

                                    <div className="cargo-stat-block">
                                        <span className="cargo-stat-label">Capacity</span>
                                        <span className="cargo-stat-value">{offer.requiredCapacity}</span>
                                    </div>

                                    <div className="cargo-stat-block">
                                        <span className="cargo-stat-label">Game Days</span>
                                        <span className="cargo-stat-value">{offer.requiredTicks}</span>
                                    </div>

                                    <div className="cargo-stat-block">
                                        <span className="cargo-stat-label">Fuel</span>
                                        <span className="cargo-stat-value">{offer.fuelConsumption}</span>
                                    </div>

                                    <div className="cargo-stat-block">
                                        <span className="cargo-stat-label">Condition</span>
                                        <span className="cargo-stat-value">{offer.conditionDamage}</span>
                                    </div>

                                    <div className="cargo-stat-block">
                                        <span className="cargo-stat-label">Type</span>
                                        <span className="cargo-stat-value">
                                            {offer.type.replace("_", " ")}
                                        </span>
                                    </div>

                                    <div className="cargo-stat-block">
                                        <span className="cargo-stat-label">Risk</span>
                                        <span className="cargo-stat-value">{offer.riskLevel}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default CargoOffersPage;