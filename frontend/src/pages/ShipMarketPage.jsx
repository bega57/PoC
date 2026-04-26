import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import api from "../api/api";
import cheapSide from "../assets/ships/cheapSide.png";
import middleSide from "../assets/ships/middleSide.png";
import expensiveSide from "../assets/ships/expensiveSide.png";
import "./ShipMarketPage.css";

function ShipMarketPage() {
    const navigate = useNavigate();
    const { sessionCode } = useParams();

    const [session, setSession] = useState(null);
    const [selectedShip, setSelectedShip] = useState(null);

    const [activeTab, setActiveTab] = useState("NEW");
    const [usedShips, setUsedShips] = useState([]);

    const [showBuyModal, setShowBuyModal] = useState(false);
    const [shipName, setShipName] = useState("");
    const [companyName, setCompanyName] = useState("");
    const [message, setMessage] = useState("");
    const [toastMessage, setToastMessage] = useState("");
    const [isBuying, setIsBuying] = useState(false);

    const ships = [
        {
            id: "cheap",
            type: "CHEAP",
            name: "Cutter",
            image: cheapSide,
            price: 12000,
            speed: 30,
            capacity: 50,
            fuel: 70,
            description: "Starter ship for short routes."
        },
        {
            id: "middle",
            type: "MEDIUM",
            name: "Brigantine",
            image: middleSide,
            price: 30000,
            speed: 55,
            capacity: 100,
            fuel: 60,
            description: "Balanced ship with solid cargo."
        },
        {
            id: "expensive",
            type: "EXPENSIVE",
            name: "Galleon",
            image: expensiveSide,
            price: 60000,
            speed: 80,
            capacity: 180,
            fuel: 50,
            description: "High capacity long-distance ship."
        }
    ];

    const isBetter = (value, key) => {
        const max = Math.max(...ships.map(s => s[key]));
        return value === max;
    };

    const getPercent = (value, key) => {
        const max = Math.max(...ships.map(s => s[key]));
        return (value / max) * 100;
    };

    const getColor = (value, key) => {
        const max = Math.max(...ships.map(s => s[key]));
        const ratio = value / max;

        if (ratio > 0.8) return "#22c55e"; // green
        if (ratio > 0.5) return "#3b82f6"; // blue
        return "#64748b"; // gray
    };

    const getStock = (type) => {
        if (!session) return null;

        switch (type) {
            case "CHEAP":
                return session.cheapShipStock;
            case "MEDIUM":
                return session.mediumShipStock;
            case "EXPENSIVE":
                return session.expensiveShipStock;
            default:
                return null;
        }
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

    const getConditionColor = (value) => {
        if (value >= 70) return "#22c55e";
        if (value >= 35) return "#facc15";
        return "#ef4444";
    };

    const fetchUsedShips = async () => {
        try {
            const response = await api.get(`/ships/used/${sessionCode}`);
            setUsedShips(response.data);
        } catch (error) {
            console.error(error);
        }
    };

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

    useEffect(() => {
        fetchUsedShips();
    }, [sessionCode]);

    useEffect(() => {
        const socket = new SockJS(`${import.meta.env.VITE_API_BASE_URL}/ws`);

        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000
        });

        client.onConnect = () => {
            console.log("Ship market WebSocket connected");

            client.subscribe(`/topic/session/${sessionCode}`, async (message) => {
                const data = JSON.parse(message.body);
                console.log("SHIP MARKET WS EVENT:", data);

                if (data.type === "STOCK_UPDATED") {
                    const refreshed = await api.get(`/sessions/${sessionCode}`);
                    setSession(refreshed.data);
                }
            });
        };

        client.activate();

        return () => {
            client.deactivate();
        };
    }, [sessionCode]);

    useEffect(() => {
        if (activeTab === "NEW" && !selectedShip) {
            setSelectedShip(ships[0]);
        }
    }, [activeTab, selectedShip]);

    const showToast = (text) => {
        setToastMessage(text);

        setTimeout(() => {
            setToastMessage("");
        }, 2500);
    };

    if (!session) {
        return <div style={{ color: "white", padding: "20px" }}>Loading market...</div>;
    }

    const activePlayerId = Number(sessionStorage.getItem(`activePlayerId-${sessionCode}`));

    const currentPlayer = session?.players?.find(
        (player) => player.id === activePlayerId
    );

    const playerNeedsCompanyName = !currentPlayer?.companyName || !currentPlayer.companyName.trim();

    if (session && !currentPlayer) {
        return (
            <div style={{ color: "white", padding: "20px" }}>
                No active player found for this session.
            </div>
        );
    }

    const openBuyModal = () => {
        setMessage("");
        setShipName("");
        setCompanyName("");
        setShowBuyModal(true);
    };

    const closeBuyModal = () => {
        setMessage("");
        setShowBuyModal(false);
    };

    const handleBuyShip = async (e) => {
        e.preventDefault();
        if (!shipName.trim()) {
            setMessage("Please enter a ship name.");
            return;
        }

        if (playerNeedsCompanyName && !companyName.trim()) {
            setMessage("Please enter a company name.");
            return;
        }

        try {
            setIsBuying(true);
            setMessage("");

            if (activeTab === "NEW") {
                await api.post("/ships/buy", {
                    playerId: currentPlayer.id,
                    shipType: selectedShip.type,
                    shipName: shipName.trim(),
                    companyName: playerNeedsCompanyName ? companyName.trim() : null,
                    sessionCode: sessionCode
                });
            } else {
                await api.post(`/ships/used/${selectedShip.id}/buy`, {
                    playerId: currentPlayer.id,
                    shipName: shipName.trim(),
                    sessionCode: sessionCode
                });

                await fetchUsedShips();
            }

            const refreshed = await api.get(`/sessions/${sessionCode}`);
            setSession(refreshed.data);

            setShowBuyModal(false);
            setShipName("");
            setCompanyName("");

            if (activeTab === "NEW") {
                showToast("New ship purchased successfully.");
            } else {
                showToast("Used ship purchased successfully.");
            }
        } catch (error) {
            console.error(error);
            setMessage(
                error.response?.data?.message ||
                error.response?.data?.error ||
                "You do not have enough balance to buy this ship."
            );
        } finally {
            setIsBuying(false);
        }
    };

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
                    <h1>Ship Market</h1>
                    <p>Choose a ship and compare its stats before buying.</p>
                </header>

                <div className="market-tabs">
                    <button
                        type="button"
                        className={`market-tab ${activeTab === "NEW" ? "active" : ""}`}
                        onClick={() => {
                            setActiveTab("NEW");
                            setSelectedShip(ships[0]);
                            setMessage("");
                        }}
                    >
                        New Ships
                    </button>

                    <button
                        type="button"
                        className={`market-tab ${activeTab === "USED" ? "active" : ""}`}
                        onClick={() => {
                            setActiveTab("USED");
                            setSelectedShip(usedShips.length > 0 ? usedShips[0] : null);
                            setMessage("");
                        }}
                    >
                        Used Ships
                    </button>
                </div>

                {session && currentPlayer && (
                    <div className="player-balance-bar">
                        <span>
                            Player: {currentPlayer.username}
                            {currentPlayer.companyName ? `    | Company name: ${currentPlayer.companyName}` : ""}
                        </span>
                        <span>Balance: ${currentPlayer.balance}</span>
                    </div>
                )}

                {activeTab === "USED" && usedShips.length === 0 ? (
                    <div className="empty-market-message">
                        No used ships available yet. Check back later or buy a new ship!
                    </div>
                ) : (
                    <div className="ship-cards">
                        {(activeTab === "NEW" ? ships : usedShips).map((ship) => (
                            <button
                                key={ship.id}
                                type="button"
                                className={`ship-market-card ${selectedShip?.id === ship.id ? "active" : ""}`}
                                onClick={() => setSelectedShip(ship)}
                            >
                                <div className="ship-card-left">
                                    <div className="ship-image-box">
                                        <img
                                            src={activeTab === "NEW" ? ship.image : getShipImage(ship.type)}
                                            alt={ship.name || ship.type}
                                        />
                                    </div>
                                </div>

                                <div className="ship-card-right">
                                    <div className="ship-main-info">
                                        <div className="ship-title-row">
                                            <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                                                <h2>{activeTab === "NEW" ? ship.name : getShipDisplayName(ship.type)}</h2>

                                                {activeTab === "NEW" && isBetter(ship.capacity, "capacity") && (
                                                    <span className="badge">BEST</span>
                                                )}

                                            </div>

                                            <span className="ship-price">${ship.price}</span>

                                            {activeTab === "NEW" && session && (
                                                <span className="ship-stock">
                                    {getStock(ship.type) > 0 ? `${getStock(ship.type)} left` : "Sold out"}
                                </span>
                                            )}
                                        </div>

                                        <p className="ship-description">
                                            {activeTab === "NEW"
                                                ? ship.description
                                                : "Pre-owned ship"}
                                        </p>
                                    </div>

                                    <div className="ship-stats">
                                        <div className="stat-block">
                                            <span className="stat-label">Speed</span>
                                            <div className="bar small">
                                                <div
                                                    style={{
                                                        width: `${getPercent(ship.speed, "speed")}%`,
                                                        background: "#22c55e"
                                                    }}
                                                />
                                                <span className="bar-text">{ship.speed}</span>
                                            </div>
                                        </div>

                                        <div className="stat-block">
                                            <span className="stat-label">Capacity</span>
                                            <div className="bar small capacity-bar">
                                                <div
                                                    style={{
                                                        width: `${getPercent(ship.capacity, "capacity")}%`,
                                                        background: "#22c55e"
                                                    }}
                                                />
                                                <span className="bar-text">{ship.capacity}</span>
                                            </div>
                                        </div>

                                        <div className="stat-block">
                                            <span className="stat-label">Condition</span>
                                            <div className="bar small">
                                                <div
                                                    style={{
                                                        width: `${activeTab === "NEW" ? 100 : ship.condition}%`,
                                                        background: getConditionColor(activeTab === "NEW" ? 100 : ship.condition)
                                                    }}
                                                />
                                                <span className="bar-text">
                                    {activeTab === "NEW" ? 100 : Math.round(ship.condition)}
                                </span>
                                            </div>
                                        </div>

                                        <div className="stat-block">
                                            <span className="stat-label">Fuel</span>
                                            <div className="bar small">
                                                <div
                                                    style={{
                                                        width: `${activeTab === "NEW" ? 100 : ship.fuelLevel}%`,
                                                        background: getConditionColor(activeTab === "NEW" ? 100 : ship.fuelLevel)
                                                    }}
                                                />
                                                <span className="bar-text">
                                    {activeTab === "NEW" ? 100 : Math.round(ship.fuelLevel)}
                                </span>
                                            </div>
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
                                {activeTab === "NEW" ? selectedShip.name : getShipDisplayName(selectedShip.type)}
                                {" "}— ${selectedShip.price}
                            </p>
                        </div>

                        <button
                            className="buy-button"
                            type="button"
                            onClick={openBuyModal}
                            disabled={
                                !selectedShip ||
                                isBuying ||
                                (activeTab === "NEW" && getStock(selectedShip.type) === 0)
                            }
                        >
                            {getStock(selectedShip.type) === 0
                                ? "Sold out"
                                : `Buy ${activeTab === "NEW" ? selectedShip.name : getShipDisplayName(selectedShip.type)}`}
                        </button>
                    </div>
                )}
            </div>

            {showBuyModal && (
                <div className="modal-overlay">
                    <div className="buy-modal">
                        <h2>
                            Buy {activeTab === "NEW" ? selectedShip.name : getShipDisplayName(selectedShip.type)}
                        </h2>

                        <p className="modal-price">Price: ${selectedShip.price}</p>

                        <form onSubmit={handleBuyShip}>
                            <input
                                type="text"
                                placeholder="Enter ship name"
                                value={shipName}
                                onChange={(e) => setShipName(e.target.value)}
                            />

                            {playerNeedsCompanyName && (
                                <input
                                    type="text"
                                    placeholder="Enter company name"
                                    value={companyName}
                                    onChange={(e) => setCompanyName(e.target.value)}
                                />
                            )}

                            {message && <p className="modal-message">{message}</p>}

                            <div className="modal-actions">
                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={closeBuyModal}
                                    disabled={isBuying}
                                >
                                    Cancel
                                </button>

                                <button
                                    type="submit"
                                    className="buy-button"
                                    disabled={isBuying || getStock(selectedShip.type) === 0}
                                >
                                    {isBuying ? "Buying..." : "Confirm Purchase"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ShipMarketPage;