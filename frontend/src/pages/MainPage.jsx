import { useState } from "react";
import CreatePlayerForm from "../components/CreatePlayerForm";
import JoinSessionForm from "../components/JoinSessionForm";
import SessionDetails from "../components/SessionDetails";
import CreateSessionButton from "../components/CreateSessionButton";
import Toast from "../components/Toast";
import "../components/Toast.css";
import "./MainPage.css";

function MainPage() {
    const [player, setPlayer] = useState(null);
    const [session, setSession] = useState(null);
    const [toast, setToast] = useState({ message: "", type: "success" });

    const showToast = (message, type = "success") => {
        setToast({ message, type });

        setTimeout(() => {
            setToast({ message: "", type: "success" });
        }, 2500);
    };

    return (
        <div className="main-page">
            <div className="main-overlay"></div>

            <Toast message={toast.message} type={toast.type} />

            <div className="main-content">
                <header className="hero-section">
                    <h1>Blue Route</h1>
                    <p className="hero-subtitle">
                        Create your player, start a session, and get ready to sail.
                    </p>
                </header>

                <div className="main-grid">
                    <div className="card">
                        <CreatePlayerForm
                            onPlayerCreated={setPlayer}
                            showToast={showToast}
                        />
                    </div>

                    <div className="card">
                        <CreateSessionButton
                            player={player}
                            onSessionCreated={setSession}
                            showToast={showToast}
                        />
                    </div>

                    <div className="card">
                        <JoinSessionForm
                            player={player}
                            onSessionJoined={setSession}
                            showToast={showToast}
                        />
                    </div>

                    <div className="card session-card">
                        <SessionDetails session={session} player={player} />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default MainPage;