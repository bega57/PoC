import { useEffect, useState } from "react";
import { useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import api from "../api/api";
import "./GamePage.css";
import "leaflet/dist/leaflet.css";
import cheapShip from "../assets/ships/cheapSide.png";
import middleShip from "../assets/ships/middleSide.png";
import expensiveShip from "../assets/ships/expensiveSide.png";
import GameSidebar from "../components/game/GameSidebar";
import GameModals from "../components/game/GameModals";
import GameStatusBar from "../components/game/GameStatusBar";
import GameMap from "../components/game/GameMap";

const geoUrl = "/countries-110m.json";

function GamePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [session, setSession] = useState(null);
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const [showLeaveModal, setShowLeaveModal] = useState(false);

    const [showWelcome, setShowWelcome] = useState(() => {
        return sessionStorage.getItem(`welcomeShown-${sessionCode}`) !== "true";
    });

    const storedPlayer = JSON.parse(
        sessionStorage.getItem(`player-${sessionCode}`) || "null"
    );

    const currentPlayer = session?.players?.find(p => p.id === storedPlayer?.id) || null;

    const [selectedPort, setSelectedPort] = useState(null);

    const [showPortInstructionModal, setShowPortInstructionModal] = useState(false);

    const [voyages, setVoyages] = useState([]);

    const [selectedShip, setSelectedShip] = useState(null);

    const [ports, setPorts] = useState([]);

    const [hoveredPort, setHoveredPort] = useState(null);
    const [portCargo, setPortCargo] = useState([]);
    const [cargoCache, setCargoCache] = useState({});

    const savedPort = sessionStorage.getItem(`currentPort-${sessionCode}`);
    const shouldShowBanner =
        !showWelcome &&
        !showPortInstructionModal &&
        !savedPort;

    const [showRewardPopup, setShowRewardPopup] = useState(false);

    const [rewardAmount, setRewardAmount] = useState(0);

    const [lastFinishedVoyageId, setLastFinishedVoyageId] = useState(() => {
        const saved = sessionStorage.getItem(`lastFinishedVoyageId-${sessionCode}`);
        return saved ? Number(saved) : null;
    });

    const lastHeartbeatRef = useRef(0);
    const isFetchingRef = useRef(false);

    const sessionIdRef = useRef(null);

    const [smoothProgress, setSmoothProgress] = useState({});

    const fetchVoyagesOnly = async (sessionId, currentTick) => {
        if (!sessionId) return;

        try {
            const res = await api.get(
                `/voyages?sessionId=${sessionId}&tick=${currentTick}`
            );
            setVoyages([...res.data]);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        const interval = setInterval(() => {
            setSmoothProgress(prev => {
                const updated = { ...prev };

                voyages.forEach(v => {
                    const current = prev[v.id] ?? v.progress ?? 0;
                    const target = v.progress ?? 0;

                    updated[v.id] = current + (target - current) * 0.15;
                });

                return updated;
            });
        }, 50);

        return () => clearInterval(interval);
    }, [voyages]);

    const sendHeartbeatSafe = () => {
        if (!sessionCode || !storedPlayer?.id) return;
        const now = Date.now();

        if (now - lastHeartbeatRef.current < 5000) return;

        lastHeartbeatRef.current = now;

        api.patch(`/sessions/${sessionCode}/players/${storedPlayer.id}/heartbeat`)
            .catch(err => console.error("Heartbeat failed:", err));
    };

    const safeFetchData = async () => {
        if (isFetchingRef.current) return;

        isFetchingRef.current = true;

        try {
            await fetchData();
        } catch (err) {
            console.error("safeFetchData failed:", err);
        } finally {
            isFetchingRef.current = false;
        }
    };

    useEffect(() => {
        if (!session || !storedPlayer) return;

        const myShips = session.players
            .find(p => p.id === storedPlayer?.id)?.ships || [];

        const myShipIds = myShips.map(s => s.id);

        const newestFinishedVoyage = [...voyages]
            .filter(v =>
                myShipIds.includes(v.shipId) &&
                v.status === "FINISHED"
            )
            .sort((a, b) => b.id - a.id)[0];

        if (!newestFinishedVoyage) return;

        if (newestFinishedVoyage.id === lastFinishedVoyageId) return;

        console.log("FINISHED VOYAGE FOUND:", newestFinishedVoyage);

        setRewardAmount(newestFinishedVoyage.reward || 0);
        setLastFinishedVoyageId(newestFinishedVoyage.id);
        sessionStorage.setItem(
            `lastFinishedVoyageId-${sessionCode}`,
            String(newestFinishedVoyage.id)
        );
        setShowRewardPopup(true);
    }, [voyages, session, sessionCode, storedPlayer, lastFinishedVoyageId]);

    useEffect(() => {
        if (!session || !storedPlayer) return;

        const me = session.players.find(p => p.id === storedPlayer?.id);
        if (!me?.ships?.length) return;

        const backendShip = me.ships[0];

        setSelectedShip(backendShip);

    }, [session]);

    const fetchData = async () => {
        if (!storedPlayer?.id) return;

        try {
            const sessionRes = await api.get(`/sessions/${sessionCode}`)
            const sessionData = sessionRes.data;

            const me = sessionData.players.find(p => p.id === storedPlayer.id);

            console.log("MY SHIPS AFTER FETCH:", JSON.stringify(me?.ships, null, 2));

            setSession(sessionData);

            const voyagesRes = await api.get(
                `/voyages?sessionId=${sessionData.id}&tick=${sessionData.currentTick}`
            );
            setVoyages(voyagesRes.data);

        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        fetchData();
    }, [sessionCode]);

    useEffect(() => {
        if (!sessionCode || !storedPlayer?.id) return;

        sendHeartbeatSafe();
        const interval = setInterval(sendHeartbeatSafe, 10000);

        return () => clearInterval(interval);
    }, [sessionCode, storedPlayer?.id]);

    useEffect(() => {
        if (!sessionCode || !storedPlayer?.id) return;

        const handleActivity = () => {
            sendHeartbeatSafe();
        };

        window.addEventListener("click", handleActivity);
        window.addEventListener("keydown", handleActivity);

        return () => {
            window.removeEventListener("click", handleActivity);
            window.removeEventListener("keydown", handleActivity);
        };
    }, [sessionCode, storedPlayer?.id]);

    useEffect(() => {
        if (!sessionCode || !storedPlayer?.id) return;

        const handleVisibility = () => {
            if (document.visibilityState === "visible") {
                sendHeartbeatSafe();
            }
        };

        document.addEventListener("visibilitychange", handleVisibility);

        return () => {
            document.removeEventListener("visibilitychange", handleVisibility);
        };
    }, [sessionCode, storedPlayer?.id]);

    useEffect(() => {
        const timer = setTimeout(() => {
            setShowWelcome(false);
            sessionStorage.setItem(`welcomeShown-${sessionCode}`, "true");
        }, 60000);

        return () => clearTimeout(timer);
    }, [sessionCode]);

    useEffect(() => {
        api.get("/ports")
            .then(res => setPorts(res.data))
            .catch(err => console.error(err));
    }, []);

    useEffect(() => {
        if (session?.id) {
            sessionIdRef.current = session.id;
        }
    }, [session]);


    useEffect(() => {

        const socket = new SockJS(`${import.meta.env.VITE_API_BASE_URL}/ws`);

        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000
        });

        client.onConnect = () => {
            console.log("WebSocket connected");

            client.subscribe(`/topic/session/${sessionCode}`, async (message) => {
                const data = JSON.parse(message.body);
                console.log("RAW WS EVENT:", data);

                if (data.type === "TICK") {
                    setSession(prev =>
                        prev ? { ...prev, currentTick: data.currentTick } : prev
                    );

                    await fetchVoyagesOnly(sessionIdRef.current, data.currentTick);

                    return;
                }

                if (data.type === "VOYAGE_STARTED") {
                    console.log("VOYAGE STARTED EVENT:", data);
                    await safeFetchData();
                    return;
                }

                if (data.type === "VOYAGE_FINISHED") {
                    console.log("VOYAGE FINISHED EVENT:", data);

                    setSession(prev => {
                        if (!prev) return prev;

                        return {
                            ...prev,
                            players: prev.players.map(p => ({
                                ...p,
                                ships: p.ships.map(ship => {
                                    if (ship.id === data.shipId) {
                                        return {
                                            ...ship,
                                            currentPort: data.destinationPort,
                                            traveling: false
                                        };
                                    }
                                    return ship;
                                })
                            }))
                        };
                    });

                    setTimeout(() => {
                        safeFetchData();
                    }, 300);
                }

                if (data.type === "SESSION_PAUSED") {
                    console.log("SESSION PAUSED EVENT:", data);

                    setSession(prev =>
                        prev
                            ? { ...prev, status: data.status }
                            : prev
                    );
                    return;
                }

                if (data.type === "SESSION_RUNNING") {
                    console.log("SESSION RUNNING EVENT:", data);

                    setSession(prev =>
                        prev
                            ? { ...prev, status: data.status }
                            : prev
                    );

                    await safeFetchData();
                    return;
                }
            });
        };

        client.activate();

        return () => {
            if (client.active) {
                client.deactivate();
            }
        };

    }, [sessionCode]);

    const handlePortHover = async (port) => {
        setHoveredPort(port);

        const portName = port.name;

        if (cargoCache[portName]) {
            setPortCargo(cargoCache[portName]);
            return;
        }

        try {
            const res = await api.get(`/cargo?portName=${portName}`);
            setPortCargo(res.data);

            setCargoCache(prev => ({
                ...prev,
                [portName]: res.data
            }));
        } catch (err) {
            console.error(err);
            setPortCargo([]);
        }
    };

    const handleLeaveSession = async () => {
        if (!storedPlayer?.id) return;

        try {
            await api.post(`/sessions/${sessionCode}/leave`, {
                playerId: storedPlayer?.id,
            });

            navigate("/");
        } catch (error) {
            console.error("Failed to leave session:", error);
        }
    };

    const openLeaveModal = () => {
        setShowLeaveModal(true);
    };

    const closeLeaveModal = () => {
        setShowLeaveModal(false);
    };

    const getShipImage = (ship) => {
        switch (ship.type) {
            case "CHEAP":
                return cheapShip;
            case "MIDDLE":
                return middleShip;
            case "EXPENSIVE":
                return expensiveShip;
            default:
                return cheapShip;
        }
    };

    if (!storedPlayer) {
        return (
            <div style={{ color: "white", padding: "20px" }}>
                Session data missing. Please return to the lobby.
            </div>
        );
    }

    if (!session) {
        return (
            <div style={{ color: "white", padding: "20px" }}>
                Loading game...
            </div>
        );
    }

    const myShips = session.players
        .find(p => p.id === storedPlayer?.id)?.ships || [];

    const myShipIds = myShips.map(s => s.id);

    const myActiveVoyages = voyages.filter(
        v => myShipIds.includes(v.shipId) && v.status === "RUNNING"
    );

    const mainPort = savedPort;
    const shipPorts = myShips
        .map(ship => ship.currentPort)
        .filter(Boolean);

    const now = Date.now();

    const rewards = portCargo.map(c => c.reward);
    const minReward = rewards.length > 0 ? Math.min(...rewards) : 0;
    const maxReward = rewards.length > 0 ? Math.max(...rewards) : 0;

    const riskLevels = portCargo.map(c => c.riskLevel);

    const riskOrder = ["LOW", "MEDIUM", "HIGH"];

    const minRisk = riskLevels.length > 0
        ? riskOrder[Math.min(...riskLevels.map(r => riskOrder.indexOf(r)))]
        : "LOW";

    const maxRisk = riskLevels.length > 0
        ? riskOrder[Math.max(...riskLevels.map(r => riskOrder.indexOf(r)))]
        : "LOW";

    const riskColor =
        maxRisk === "HIGH" ? "#ef4444" :
            maxRisk === "MEDIUM" ? "#f59e0b" :
                "#22c55e";

    const boxWidth = portCargo.length > 1 ? 150 : 130;

    const offsetX = 15;
    const offsetY = hoveredPort?.latitude > 0 ? 20 : -100;

    return (
        <div className="game-container">

            <GameMap
                session={session}
                ports={ports}
                voyages={voyages}
                hoveredPort={hoveredPort}
                setHoveredPort={setHoveredPort}
                handlePortHover={handlePortHover}
                showWelcome={showWelcome}
                showPortInstruction={shouldShowBanner}
                showPortInstructionModal={showPortInstructionModal}
                setSelectedPort={setSelectedPort}
                mainPort={mainPort}
                shipPorts={shipPorts}
                myShipIds={myShipIds}
                portCargo={portCargo}
                minReward={minReward}
                maxReward={maxReward}
                minRisk={minRisk}
                maxRisk={maxRisk}
                riskColor={riskColor}
                boxWidth={boxWidth}
                offsetX={offsetX}
                offsetY={offsetY}
                getShipImage={getShipImage}
                geoUrl={geoUrl}
            />

            <GameStatusBar
                session={session}
                selectedShip={selectedShip}
                currentPlayer={currentPlayer}
                myActiveVoyages={myActiveVoyages}
                smoothProgress={smoothProgress}
            />

            <GameSidebar
                session={session}
                sidebarOpen={sidebarOpen}
                setSidebarOpen={setSidebarOpen}
                navigate={navigate}
                sessionCode={sessionCode}
                handleLeaveSession={openLeaveModal}
            />

            <GameModals
                showWelcome={showWelcome}
                setShowWelcome={setShowWelcome}
                selectedPort={selectedPort}
                setSelectedPort={setSelectedPort}

                showPortInstructionModal={showPortInstructionModal}
                setShowPortInstructionModal={setShowPortInstructionModal}

                showRewardPopup={showRewardPopup}
                setShowRewardPopup={setShowRewardPopup}
                rewardAmount={rewardAmount}
                currentPlayer={currentPlayer}
                storedPlayer={storedPlayer}
                setSelectedShip={setSelectedShip}
                sessionCode={sessionCode}

                showLeaveModal={showLeaveModal}
                setShowLeaveModal={setShowLeaveModal}
                handleLeaveSession={handleLeaveSession}
                closeLeaveModal={closeLeaveModal}
                setSession={setSession}
            />

        </div>
    );
}

export default GamePage;