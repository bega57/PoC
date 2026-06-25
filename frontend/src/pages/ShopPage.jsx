import { useNavigate, useParams } from "react-router-dom";
import { useContext, useEffect, useState } from "react";
import { GameContext } from "../layouts/AppLayout";
import api from "../api/api";
import "./ShopPage.css";
import "./ShipMarketPage.css";

export default function ShopPage() {
    const navigate = useNavigate();
    const { sessionCode } = useParams();
    const { player, setPlayer } = useContext(GameContext);

    const [shopItems, setShopItems] = useState([]);
    const [inventory, setInventory] = useState([]);
    const [message, setMessage] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        api.get("/shop/items").then(res => setShopItems(res.data || []));
    }, []);

    useEffect(() => {
        if (player?.id) {
            api.get(`/shop/inventory/${player.id}`).then(res => setInventory(res.data || []));
        }
    }, [player?.id]);

    const refreshPlayer = async () => {
        const res = await api.get(`/players/${player.id}`);
        setPlayer(res.data);
    };

    const handleBuy = async (item) => {
        if (loading) return;
        setLoading(true);
        setMessage(null);
        try {
            const res = await api.post(`/shop/buy/${player.id}`, { powerUpType: item.type });
            setInventory(res.data);
            await refreshPlayer();
            setMessage({ type: "success", text: `✅ Bought ${item.emoji} ${item.displayName}!` });
        } catch (err) {
            const msg = err.response?.data?.message || err.response?.data || "Purchase failed";
            setMessage({ type: "error", text: typeof msg === "string" ? msg : "Not enough balance!" });
        } finally {
            setLoading(false);
            setTimeout(() => setMessage(null), 3000);
        }
    };

    const canAfford = (price) => player && player.balance >= price;

    return (
        <div className="shop-page">
            <div className="shop-overlay" />
            <div className="shop-content">

                <button className="back-button" onClick={() => navigate(`/session/${sessionCode}/game`)}>
                    🡸 Back to Game
                </button>

                <header className="shop-header">
                    <h1>🛒 Mini Market</h1>
                    <p>Buy power-ups to give your crew an edge on the next voyage.</p>
                </header>

                <div className="shop-balance-bar">
                    <span>💰 Balance: <strong>{Math.floor(player?.balance || 0).toLocaleString("de-DE")} $</strong></span>
                    <span>⭐ Points: <strong>{(player?.points || 0).toLocaleString("de-DE")}</strong></span>
                </div>

                {message && (
                    <div className={`shop-message ${message.type}`}>{message.text}</div>
                )}

                <div className="shop-section-title">🏪 Available Power-Ups</div>
                <div className="shop-grid">
                    {shopItems.map(item => (
                        <div key={item.type} className="shop-item-card">
                            <div className="shop-item-emoji">{item.emoji}</div>
                            <div className="shop-item-name">{item.displayName}</div>
                            <div className="shop-item-desc">{item.description}</div>
                            <div className="shop-item-price">💰 {item.price.toLocaleString("de-DE")} $</div>
                            <button
                                className="shop-buy-btn"
                                onClick={() => handleBuy(item)}
                                disabled={loading || !canAfford(item.price)}
                            >
                                {canAfford(item.price) ? "Buy" : "Can't afford"}
                            </button>
                        </div>
                    ))}
                </div>

                <div className="shop-section-title">🎒 Your Inventory</div>
                {inventory.length === 0 ? (
                    <div className="shop-empty">No power-ups yet. Go buy some!</div>
                ) : (
                    <div className="inventory-grid">
                        {inventory.map(item => (
                            <div key={item.type} className="inventory-item-card">
                                <div className="shop-item-emoji">{item.emoji}</div>
                                <div className="shop-item-name">{item.displayName}</div>
                                <div className="shop-item-desc">{item.description}</div>
                                <div className="inventory-item-qty">×{item.quantity}</div>
                            </div>
                        ))}
                    </div>
                )}

                <div style={{ textAlign: "center", marginTop: "16px", color: "#6b7280", fontSize: "14px" }}>
                    Activate power-ups on the <strong style={{ color: "#a78bfa" }}>Voyage page</strong> before departure.
                </div>

            </div>
        </div>
    );
}
