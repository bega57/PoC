import { useEffect, useState, useRef, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
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
import { GameContext } from "../layouts/AppLayout";
import VoyageEventModal from "../components/VoyageEventModal";
import Toast from "../components/Toast";

const geoUrl = "/countries-110m.json";

function GamePage() {
    const { sessionCode } = useParams();
    const navigate = useNavigate();

    const [sidebarOpen, setSidebarOpen] = useState(true);
    const [showLeaveModal, setShowLeaveModal] = useState(false);

    const [showWelcome, setShowWelcome] = useState(() => {
        return sessionStorage.getItem(`welcomeShown-${sessionCode}`) !== "true";
    });

    const [selectedPort, setSelectedPort] = useState(null);

    const [showPortInstructionModal, setShowPortInstructionModal] = useState(false);

    const [voyages, setVoyages] = useState([]);

    const [selectedShip, setSelectedShip] = useState(null);

    const [ports, setPorts] = useState([]);
    const [ships, setShips] = useState([]);
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

    const { session, setSession, player, setPlayer } = useContext(GameContext);
    const currentPlayer = player;
    const [finishedVoyageInfo, setFinishedVoyageInfo] = useState(null);

    const [leaderboard, setLeaderboard] = useState(() => {
        const saved = sessionStorage.getItem(`leaderboard-${sessionCode}`);
        return saved ? JSON.parse(saved) : null;
    });

    const [activeEvent, setActiveEvent] = useState(null);
    const [eventLoading, setEventLoading] = useState(false);

    const [toastMessage, setToastMessage] = useState("");
    const [toastType, setToastType] = useState("success");

    const getLocalLeaderboard = () => {
        return JSON.parse(localStorage.getItem("leaderboard") || "[]");
    };

    const saveScore = (score) => {
        const existing = getLocalLeaderboard();

        const newEntry = {
            username: currentPlayer?.username,
            score: score
        };

        const filtered = existing.filter(e => e.username !== newEntry.username);

        const updated = [...filtered, newEntry]
            .sort((a, b) => b.score - a.score)
            .slice(0, 10);

        localStorage.setItem("leaderboard", JSON.stringify(updated));
    };

    const [lastFinishedVoyageId, setLastFinishedVoyageId] = useState(() => {
        const saved = sessionStorage.getItem(`lastFinishedVoyageId-${sessionCode}`);
        return saved ? Number(saved) : null;
    });

    const lastHeartbeatRef = useRef(0);
    const isFetchingRef = useRef(false);

    const sessionIdRef = useRef(null);

    const fetchVoyagesOnly = async (sessionId, currentTick) => {
        if (!sessionId) return;

        try {
            const res = await api.get(
                `/voyages?sessionId=${sessionId}&currentTick=${currentTick}`
            );
            setVoyages(res.data);
        } catch (err) {
            console.error(err);
        }
    };

    const sendHeartbeatSafe = () => {
        if (!sessionCode || !player?.id) return;
        const now = Date.now();

        if (now - lastHeartbeatRef.current < 5000) return;

        lastHeartbeatRef.current = now;

        api.patch(`/sessions/${sessionCode}/players/${player.id}/heartbeat`)
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
        if (!session || !player) return;

        const myShips = session.players
            .find(p => p.id === player?.id)?.ships || [];

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
    }, [voyages, session, sessionCode, player, lastFinishedVoyageId]);

    useEffect(() => {
        if (!session || !player) return;

        const me = session.players.find(p => p.id === player?.id);

        if (!me?.ships?.length) return;

        const backendShip = me.ships[0];

        setSelectedShip(backendShip);

    }, [session]);

    const fetchData = async () => {

        if (!player?.id) return;
        try {
            const sessionRes = await api.get(`/sessions/${sessionCode}`);
            const sessionData = sessionRes.data;

            setSession(sessionData);

            const shipsRes = await api.get(`/ships/player/${player.id}`);
            setShips(shipsRes.data);

            const me = sessionData.players.find(p => p.id === player.id);
            if (!me) return;

            setPlayer(prev => ({
                ...prev,
                balance: me.balance,
                companyName: me.companyName
            }));

            const voyagesRes = await api.get(
                `/voyages?sessionId=${sessionData.id}&currentTick=${sessionData.currentTick}`
            );
            setVoyages(voyagesRes.data);

            const leaderboardRes = await api.get(`/sessions/${sessionCode}/leaderboard`);

            setLeaderboard(leaderboardRes.data);

            sessionStorage.setItem(
                `leaderboard-${sessionCode}`,
                JSON.stringify(leaderboardRes.data)
            );

        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        if (!player) return;
        fetchData();
    }, [sessionCode, player]);

    useEffect(() => {
        if (!sessionCode || !player?.id) return;

        sendHeartbeatSafe();
        const interval = setInterval(sendHeartbeatSafe, 10000);

        return () => clearInterval(interval);
    }, [sessionCode, player?.id]);

    useEffect(() => {
        if (!sessionCode || !player?.id) return;

        const handleActivity = () => {
            sendHeartbeatSafe();
        };

        window.addEventListener("click", handleActivity);
        window.addEventListener("keydown", handleActivity);

        return () => {
            window.removeEventListener("click", handleActivity);
            window.removeEventListener("keydown", handleActivity);
        };
    }, [sessionCode, player?.id]);

    useEffect(() => {
        if (!sessionCode || !player?.id) return;

        const handleVisibility = () => {
            if (document.visibilityState === "visible") {
                sendHeartbeatSafe();
            }
        };

        document.addEventListener("visibilitychange", handleVisibility);

        return () => {
            document.removeEventListener("visibilitychange", handleVisibility);
        };
    }, [sessionCode, player?.id]);

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

    const isMultiplayer = session?.maxPlayers > 1;

    useEffect(() => {
        if (!isMultiplayer) {
            const local = getLocalLeaderboard();

            setLeaderboard(prev =>
                (prev?.length ?? 0) > 0 ? prev : local
            );
        }
    }, [isMultiplayer]);

    useEffect(() => {
        if (!toastMessage) return;

        const timer = setTimeout(() => {
            setToastMessage("");
        }, 3000);

        return () => clearTimeout(timer);
    }, [toastMessage]);

    useEffect(() => {

        const API_BASE_URL =
            import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

        const socket = new SockJS('http://localhost:8084/ws');

        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000
        });

        client.onConnect = () => {
            console.log("WebSocket connected");

            client.subscribe(`/topic/session/${sessionCode}`, async (message) => {
                const data = JSON.parse(message.body);
                console.log("RAW WS EVENT:", data);

                if (data.eventType && data.voyageId) {
                    setActiveEvent(data);
                    return;
                }

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

                    setRewardAmount(data.reward || 0);
                    setFinishedVoyageInfo(data);
                    setLastFinishedVoyageId(data.voyageId);
                    sessionStorage.setItem(
                        `lastFinishedVoyageId-${sessionCode}`,
                        String(data.voyageId)
                    );
                    setShowRewardPopup(true);

                    setShips(prevShips =>
                        prevShips.map(ship => {
                            if (ship.id === data.shipId) {
                                return {
                                    ...ship,
                                    currentPort: data.destinationPort,
                                    traveling: false
                                };
                            }

                            return ship;
                        })
                    );

                    setSession(prev => {
                        if (!prev) return prev;

                        if (prev.maxPlayers === 1) {
                            const myPlayer = prev.players.find(p => p.id === player.id);

                            if (myPlayer) {
                                saveScore(myPlayer.balance);
                            }
                        }

                        return prev;
                    });

                    setTimeout(() => {
                        safeFetchData();
                    }, 300);

                    return;
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

                if (data.type === "LEADERBOARD_UPDATE") {

                    setLeaderboard(prev => {
                        if (!data.leaderboard?.length) return prev;

                        const updated =
                            JSON.stringify(prev) === JSON.stringify(data.leaderboard)
                                ? prev
                                : data.leaderboard;

                        sessionStorage.setItem(
                            `leaderboard-${sessionCode}`,
                            JSON.stringify(updated)
                        );

                        return updated;
                    });
                }
            });
        };

        client.activate();

        return () => {
            client.deactivate();
        };
    }, [sessionCode, player?.id]);


    const handleEventChoice = async (optionIndex) => {
        if (!activeEvent) {
            return;
        }

        const optionMap = ["OPTION_A", "OPTION_B", "OPTION_C"];
        const selectedOption = optionMap[optionIndex];

        try {
            setEventLoading(true);

            const response = await api.post(
                `/voyage-events/${activeEvent.voyageId}/resolve`,
                { selectedOption }
            );

            setToastMessage(response.data.message);
            setToastType("success");
            setActiveEvent(null);

            await fetchData();
        } catch (error) {
            console.error(error);
            setToastMessage(error.response?.data?.message || "Failed to resolve event.");
            setToastType("error");
        } finally {
            setEventLoading(false);
        }
    };

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
        if (!player?.id) return;

        try {
            await api.post(`/sessions/${sessionCode}/leave`, {
                playerId: player?.id,
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

    if (!player) return null;

    if (!session) {
        return (
            <div style={{ color: "white", padding: "20px" }}>
                Loading game...
            </div>
        );
    }

    const myShips = ships;

    const myShipIds = myShips.map(s => s.id);

    const myActiveVoyages = voyages.filter(
        v => myShipIds.includes(v.shipId) && v.status === "RUNNING"
    );

    const mainPort = savedPort;
    const shipPorts = myShips
        .map(ship => ship.currentPort)
        .filter(Boolean);

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
                ships={ships}
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
            />

            <GameSidebar
                session={session}
                leaderboard={leaderboard}
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
                finishedVoyageInfo={finishedVoyageInfo}
                setFinishedVoyageInfo={setFinishedVoyageInfo}
                currentPlayer={currentPlayer}
                setSelectedShip={setSelectedShip}
                sessionCode={sessionCode}
                storedPlayer={player}
                setSession={setSession}
                showLeaveModal={showLeaveModal}
                setShowLeaveModal={setShowLeaveModal}
                handleLeaveSession={handleLeaveSession}
                closeLeaveModal={closeLeaveModal}
            />

            <VoyageEventModal
                event={activeEvent}
                onSelect={handleEventChoice}
                loading={eventLoading}
            />

            <Toast message={toastMessage} type={toastType} />

        </div>
    );
}

export default GamePage;