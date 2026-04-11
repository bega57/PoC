import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
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

    const [showWelcome, setShowWelcome] = useState(() => {
        return sessionStorage.getItem(`welcomeShown-${sessionCode}`) !== "true";
    });

    const storedPlayer = JSON.parse(
        sessionStorage.getItem(`player-${sessionCode}`) || "null"
    );

    const currentPlayer = session?.players?.find(p => p.id === storedPlayer?.id) || null;

    const [selectedPort, setSelectedPort] = useState(null);

    const [showPortInstruction, setShowPortInstruction] = useState(false);

    const [voyages, setVoyages] = useState([]);

    const [selectedShip, setSelectedShip] = useState(null);

    const [ports, setPorts] = useState([]);

    const [hoveredPort, setHoveredPort] = useState(null);
    const [portCargo, setPortCargo] = useState([]);
    const [cargoCache, setCargoCache] = useState({});

    const savedPort = localStorage.getItem(`currentPort-${sessionCode}`);

    const [showRewardPopup, setShowRewardPopup] = useState(false);

    const [rewardAmount, setRewardAmount] = useState(0);

    const [lastFinishedVoyageId, setLastFinishedVoyageId] = useState(() => {
        const saved = sessionStorage.getItem(`lastFinishedVoyageId-${sessionCode}`);
        return saved ? Number(saved) : null;
    });



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

        setSelectedShip({
            ...backendShip,
            currentPort: backendShip.currentPort // 👈 IMMER backend truth
        });

    }, [session]);


    useEffect(() => {
        const fetchData = () => {
            api.get(`/sessions/${sessionCode}`)
                .then((res) => {
                    setSession(res.data);

                })
                .catch((err) => console.error(err));

            api.get(`/voyages?sessionId=${session?.id}`)
                .then(res => setVoyages(res.data))
                .catch(err => console.error(err));
        };

        fetchData();
        const interval = setInterval(() => {
            fetchData();
        }, 2000);

        return () => clearInterval(interval);
    }, [sessionCode]);


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

    useEffect(() => {

        if (savedPort) {
            setShowPortInstruction(false);
        }
    }, [sessionCode]);

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

    if (!session) {
        return <div style={{ color: "white", padding: "20px" }}>Loading game...</div>;
    }

    const myShips = session.players
        .find(p => p.id === storedPlayer?.id)?.ships || [];


    const myShipIds = myShips.map(s => s.id);

    const mainPort = savedPort;
    const shipPorts = myShips
        .map(ship => ship.currentPort)
        .filter(Boolean);

    const now = Date.now();

    const myActiveVoyage = voyages.find(
        v => myShipIds.includes(v.shipId) && v.status === "RUNNING"
    );

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
                showPortInstruction={showPortInstruction}
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
                myActiveVoyage={myActiveVoyage}
            />

            <GameSidebar
                session={session}
                sidebarOpen={sidebarOpen}
                setSidebarOpen={setSidebarOpen}
                navigate={navigate}
                sessionCode={sessionCode}
                handleLeaveSession={handleLeaveSession}
            />

            <GameModals
                showWelcome={showWelcome}
                setShowWelcome={setShowWelcome}
                selectedPort={selectedPort}
                setSelectedPort={setSelectedPort}
                showPortInstruction={showPortInstruction}
                setShowPortInstruction={setShowPortInstruction}
                showRewardPopup={showRewardPopup}
                setShowRewardPopup={setShowRewardPopup}
                rewardAmount={rewardAmount}
                currentPlayer={currentPlayer}
                storedPlayer={storedPlayer}
                setSelectedShip={setSelectedShip}
                sessionCode={sessionCode}
            />

        </div>
    );
}

export default GamePage;