import { useNavigate } from "react-router-dom";
import "./LandingPage.css";
import logo from "../assets/blueroutelogo.png";

function LandingPage() {
    const navigate = useNavigate();

    return (
        <div className="landing-page">


            <div className="ships-bg">
                <div className="ship ship1"></div>
                <div className="ship ship2"></div>
            </div>


            <div className="landing-card">

                <img src={logo} alt="logo" className="logo" />

                <div className="landing-brand">
                    <span className="blue">BLUE</span>
                    <span className="white">ROUTE</span>
                </div>

                <p className="subtitle">
                    Build your fleet. Conquer the seas.
                </p>

                <div className="landing-buttons">
                    <button onClick={() => navigate("/info")}>
                        New here
                    </button>

                    <button onClick={() => navigate("/main")} className="secondary">
                        Start
                    </button>
                </div>

            </div>
        </div>
    );
}

export default LandingPage;