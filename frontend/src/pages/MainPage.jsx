import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import api from "../api/api";
import CreatePlayerForm from "../components/CreatePlayerForm";
import JoinSessionForm from "../components/JoinSessionForm";
import ResumeSessionForm from "../components/ResumeSessionForm";
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

    const fetchSession = async () => {
        if (!session?.sessionCode) return;

        try {
            const response = await api.get(`/sessions/${session.sessionCode}`);
            setSession(response.data);
        } catch (error) {
            console.error("Failed to fetch session:", error);
        }
    };

    useEffect(() => {
        if (!session?.sessionCode) return;

        const socket = new SockJS(`${import.meta.env.VITE_API_BASE_URL}/ws`);

        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000
        });

        client.onConnect = () => {
            console.log("MainPage WebSocket connected");

            client.subscribe(`/topic/session/${session.sessionCode}`, async (message) => {
                const data = JSON.parse(message.body);
                console.log("MAINPAGE WS EVENT:", data);

                if (
                    data.type === "TICK" ||
                    data.type === "VOYAGE_FINISHED" ||
                    data.type === "SESSION_PAUSED" ||
                    data.type === "SESSION_RUNNING"
                ) {
                    await fetchSession();
                }
            });
        };

        client.activate();

        return () => {
            client.deactivate();
        };
    }, [session?.sessionCode]);

    useEffect(() => {
        const handleBeforeUnload = () => {
            if (!session?.sessionCode || !player?.id) return;

            localStorage.setItem(`sessionCode-${player.id}`, session.sessionCode);
            localStorage.setItem("lastSessionCode", session.sessionCode);
            localStorage.setItem("lastPlayerId", player.id);
        };

        window.addEventListener("beforeunload", handleBeforeUnload);

        return () => {
            window.removeEventListener("beforeunload", handleBeforeUnload);
        };
    }, [session, player]);

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

                    <div className="card">
                        <ResumeSessionForm
                            onSessionResumed={setSession}
                            onPlayerResumed={setPlayer}
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