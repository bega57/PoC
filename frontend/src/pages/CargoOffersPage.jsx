import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "./CargoOffersPage.css";

function CargoOffersPage() {
    const { sessionCode } = useParams();

    const [offers, setOffers] = useState([]);
    const [fromPort, setFromPort] = useState("");
    const [sortBy, setSortBy] = useState("");

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
        const ports = [...new Set(offers.map((offer) => offer.fromPort))];
        return ports.sort((a, b) => a.localeCompare(b));
    }, [offers]);

    const filteredOffers = useMemo(() => {
        let data = [...offers];

        if (fromPort) {
            data = data.filter((offer) => offer.fromPort === fromPort);
        }

        if (sortBy === "reward") {
            data.sort((a, b) => b.reward - a.reward);
        }

        if (sortBy === "risk") {
            data.sort((a, b) => a.riskLevel.localeCompare(b.riskLevel));
        }

        return data;
    }, [offers, fromPort, sortBy]);

    return (
        <div className="page-container">
            <h1>Cargo Offers</h1>

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

            <div className="cargo-offers-grid">
                {filteredOffers.map((offer) => (
                    <div key={offer.id} className="cargo-offer-card">
                        <div className="cargo-offer-left">
                            <span className="cargo-label">Port</span>
                            <h3>{offer.fromPort}</h3>
                        </div>

                        <div className="cargo-offer-right">
                            <h3>{offer.name}</h3>
                            <p><strong>To:</strong> {offer.toPort}</p>
                            <p><strong>Reward:</strong> {offer.reward}</p>
                            <p><strong>Risk:</strong> {offer.riskLevel}</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default CargoOffersPage;