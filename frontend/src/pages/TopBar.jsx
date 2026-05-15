import "./TopBar.css";
import { memo } from "react";
import logo from "../assets/blueroutelogo.png";

function TopBar({ session, player }) {
    return (
        <div className="topbar">

            {/* LEFT */}
            <div className="tb-left">

                <div className="brand">
                    <img src={logo} alt="logo" className="logo-img" />

                    <span className="brand-text">
                        <span className="blue">BLUE</span>
                        <span className="white">ROUTE</span>
                    </span>
                </div>

                <div
                    className="chip session"
                    onClick={() => navigator.clipboard.writeText(session.sessionCode)}
                >
                    🔑 {session.sessionCode}
                </div>

            </div>

            {/* CENTER */}
            <div className="tb-center">
                ⏱ Day {session.currentTick}
            </div>

            {/* RIGHT */}
            <div className="tb-right">

                <div className="card company">
                    <div className="card-label">COMPANY</div>
                    <div className="card-main">
                        🏢 {player.companyName}
                    </div>
                </div>

                <div className="card balance">
                    <div className="card-label">BALANCE</div>
                    <div className="card-main">
                        💰 {Math.floor(player.balance).toLocaleString("de-DE")} $
                    </div>
                </div>

                <div className="player-info">
                    👤 {player.username} #{player.id}
                </div>

            </div>
        </div>
    );
}

export default memo(TopBar);