import { Routes, Route } from "react-router-dom";
import MainPage from "./pages/MainPage";
import GamePage from "./pages/GamePage";
import ShipMarketPage from "./pages/ShipMarketPage";
import ShipMarketMenuPage from "./pages/ShipMarketMenuPage";
import CompanyPage from "./pages/CompanyPage";
import VoyagePage from "./pages/VoyagePage";

import "./App.css";
import SellShipPage from "./pages/SellShipPage.jsx";

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
        </Routes>
    );
}

export default App;