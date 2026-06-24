import { Routes, Route } from "react-router-dom";
import LandingPage from "./pages/LandingPage";
import MainPage from "./pages/MainPage";
import GamePage from "./pages/GamePage";
import ShipMarketPage from "./pages/ShipMarketPage";
import ShipMarketMenuPage from "./pages/ShipMarketMenuPage";
import CompanyPage from "./pages/CompanyPage";
import VoyagePage from "./pages/VoyagePage";
import SellShipPage from "./pages/SellShipPage";
import ShipRefuelPage from "./pages/ShipRefuelPage";
import ShipRepairPage from "./pages/ShipRepairPage";
import CargoOffersPage from "./pages/CargoOffersPage";
import InfoPage from "./pages/InfoPage";
import AppLayout from "./layouts/AppLayout";
import ShopPage from "./pages/ShopPage";

function App() {
    return (
        <Routes>

            <Route path="/" element={<LandingPage />} />
            <Route path="/info" element={<InfoPage />} />

            <Route path="/main" element={<MainPage />} />

            <Route path="/session/:sessionCode" element={<AppLayout />}>
                <Route path="game" element={<GamePage />} />
                <Route path="market" element={<ShipMarketMenuPage />} />
                <Route path="market/buy" element={<ShipMarketPage />} />
                <Route path="market/sell" element={<SellShipPage />} />
                <Route path="market/refuel" element={<ShipRefuelPage />} />
                <Route path="market/repair" element={<ShipRepairPage />} />
                <Route path="company" element={<CompanyPage />} />
                <Route path="voyage" element={<VoyagePage />} />
                <Route path="cargo-offers" element={<CargoOffersPage />} />
                <Route path="shop" element={<ShopPage />} />
            </Route>

        </Routes>
    );
}

export default App;