import { Routes, Route } from "react-router-dom";
import MainPage from "./pages/MainPage";
import GamePage from "./pages/GamePage";
import ShipMarketPage from "./pages/ShipMarketPage";
import ShipMarketMenuPage from "./pages/ShipMarketMenuPage";
import CompanyPage from "./pages/CompanyPage";
import VoyagePage from "./pages/VoyagePage";

import "./App.css";
import SellShipPage from "./pages/SellShipPage.jsx";
import ShipRefuelPage from "./pages/ShipRefuelPage.jsx";
import ShipRepairPage from "./pages/ShipRepairPage.jsx";
import CargoOffersPage from "./pages/CargoOffersPage";

function App() {
    return (
        <Routes>
            <Route path="/" element={<MainPage />} />
            <Route path="/game/:sessionCode" element={<GamePage />} />
            <Route path="/market/:sessionCode" element={<ShipMarketMenuPage />} />
            <Route path="/market/:sessionCode/buy" element={<ShipMarketPage />} />
            <Route path="/company/:sessionCode" element={<CompanyPage />} />
            <Route path="/market/:sessionCode/sell" element={<SellShipPage />} />
            <Route path="/voyage/:sessionCode" element={<VoyagePage />} />
            <Route path="/market/:sessionCode/refuel" element={<ShipRefuelPage />} />
            <Route path="/market/:sessionCode/repair" element={<ShipRepairPage />} />
            <Route path="/cargo-offers/:sessionCode"  element={<CargoOffersPage />}
            />
        </Routes>
    );
}

export default App;