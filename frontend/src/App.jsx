import { Routes, Route } from "react-router-dom";
import MainPage from "./pages/MainPage";
import GamePage from "./pages/GamePage";
import ShipMarketPage from "./pages/ShipMarketPage";

import "./App.css";

function App() {
    return (
        <Routes>
            <Route path="/" element={<MainPage />} />
            <Route path="/game/:sessionCode" element={<GamePage />} />
            <Route path="/market/:sessionCode" element={<ShipMarketPage />} />
        </Routes>
    );
}

export default App;