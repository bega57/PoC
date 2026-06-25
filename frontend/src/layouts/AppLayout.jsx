import { Outlet, useParams } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import TopBar from "../pages/TopBar";
import api, { API_BASE_URL } from "../api/api";
import { createContext } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const GameContext = createContext(null);

function AppLayout() {
    const { sessionCode } = useParams();

    const activePlayerId = sessionStorage.getItem(
        `activePlayerId-${sessionCode}`
    );

    const [session, setSession] = useState(null);
    const [player, setPlayer] = useState(null);
    const voyageMusicRef = useRef(null);

    const playVoyageMusic = (audioSrc) => {
        if (voyageMusicRef.current) {
            voyageMusicRef.current.pause();
            voyageMusicRef.current = null;
        }
        const audio = new Audio(audioSrc);
        audio.loop = true;
        audio.play().catch(() => {});
        voyageMusicRef.current = audio;
    };

    const stopVoyageMusic = () => {
        if (voyageMusicRef.current) {
            voyageMusicRef.current.pause();
            voyageMusicRef.current = null;
        }
    };

    useEffect(() => {
        if (!sessionCode || !activePlayerId) return;

        async function fetchData() {
            const [sessionRes, playerRes] = await Promise.all([
                api.get(`/sessions/${sessionCode}`),
                api.get(`/players/${activePlayerId}`)
            ]);

            setSession(sessionRes.data);
            setPlayer(playerRes.data);
            console.log("INITIAL PLAYER", playerRes.data);
            console.log("INITIAL SESSION", sessionRes.data);
        }

        fetchData();
    }, [sessionCode,activePlayerId]);

    useEffect(() => {
        if (!sessionCode || !activePlayerId) return;

        const socket = new SockJS(`${API_BASE_URL}/ws`);

        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000
        });

        client.onConnect = () => {
            console.log("GLOBAL WS CONNECTED");

            client.subscribe(`/topic/session/${sessionCode}`, async (message) => {
                const data = JSON.parse(message.body);

                console.log("GLOBAL EVENT:", data);

                if (data.type === "TICK") {
                    setSession(prev =>
                        prev ? { ...prev, currentTick: data.currentTick } : prev
                    );

                    return;
                }

                if (data.type === "VOYAGE_FINISHED") {
                    stopVoyageMusic();
                }

                if (
                    data.type === "SHIP_BOUGHT" ||
                    data.type === "SHIP_SOLD" ||
                    data.type === "SHIP_REPAIRED" ||
                    data.type === "SHIP_REFUELED" ||
                    data.type === "VOYAGE_FINISHED"
                ) {

                    try {
                        const [sessionRes, playerRes] = await Promise.all([
                            api.get(`/sessions/${sessionCode}`),
                            api.get(`/players/${activePlayerId}`)
                        ]);

                        setSession(sessionRes.data);
                        setPlayer(playerRes.data);
                        console.log("PLAYER", playerRes.data);
                        console.log("SESSION", sessionRes.data);

                    } catch (err) {
                        console.error(err);
                    }
                }
            });
        };

        client.activate();

        return () => {
            client.deactivate();
        };

    }, [sessionCode,activePlayerId]);

    return (
        <GameContext.Provider value={{ session, setSession, player, setPlayer, playVoyageMusic, stopVoyageMusic }}>
            {session && player && (
                <TopBar session={session} player={player} />
            )}

            <div className="app-content">
                {!session || !player ? "Loading..." : <Outlet />}
            </div>
        </GameContext.Provider>
    );
}

export default AppLayout;