import {
    ComposableMap,
    Geographies,
    Geography,
    Marker
} from "react-simple-maps";
import "./GameMap.css";

function getPositionOnRoute(route, progress) {
    if (!route || route.length < 2) return null;

    const totalSegments = route.length - 1;
    const segmentProgress = progress * totalSegments;

    const index = Math.floor(segmentProgress);
    const localProgress = segmentProgress - index;

    const from = route[index];
    const to = route[index + 1] || from;

    const lng = from[0] + (to[0] - from[0]) * localProgress;
    const lat = from[1] + (to[1] - from[1]) * localProgress;

    return [lng, lat];
}

function GameMap({
                     session,
                     ports,
                     voyages,
                     smoothProgress,
                     hoveredPort,
                     setHoveredPort,
                     handlePortHover,
                     showWelcome,
                     showPortInstruction,
                     showPortInstructionModal,
                     setSelectedPort,
                     mainPort,
                     shipPorts,
                     finishedPorts,
                     myShipIds,
                     portCargo,
                     minReward,
                     maxReward,
                     minRisk,
                     maxRisk,
                     riskColor,
                     boxWidth,
                     offsetX,
                     offsetY,
                     getShipImage,
                     geoUrl
                 }) {

    return (
        <div className="map-container">

            {showPortInstruction && (
                <div className="port-instruction-popup">

                    <div className="port-instruction-title">
                        ⚠️ Main Port Required
                    </div>
                    <div className="port-instruction-subtext">
                        You cannot change this later
                    </div>

                </div>
            )}

            <ComposableMap
                projection="geoEqualEarth"
                projectionConfig={{ scale: 220 }}
                style={{ width: "100%", height: "100%" }}
            >

                <Geographies geography={geoUrl}>
                    {({ geographies }) =>
                        geographies.map((geo) => (
                            <Geography
                                key={geo.rsmKey}
                                geography={geo}
                                fill="#18314f"
                                stroke="#2f4f73"
                                style={{
                                    default: {
                                        fill: "#18314f",
                                        outline: "none",
                                        transition: "0.2s"
                                    },
                                    hover: {
                                        fill: "#18314f",
                                        outline: "none"
                                    },
                                    pressed: { outline: "none" }
                                }}
                            />
                        ))
                    }
                </Geographies>

                {ports.map((port) => (
                    <Marker
                        key={port.name}
                        coordinates={[port.longitude, port.latitude]}
                        onMouseEnter={() => handlePortHover(port)}
                        onMouseLeave={() => {
                            setTimeout(() => setHoveredPort(null), 80);
                        }}
                        onClick={() => {
                            if (mainPort) return;

                            if (!showWelcome && !showPortInstructionModal) {
                                setSelectedPort(port.name);
                            }
                        }}
                    >

                        <>
                            {port.name === mainPort && (
                                <circle
                                    r={12}
                                    fill="rgba(34,211,238,0.2)"
                                />
                            )}

                            <circle
                                className="port-dot"
                                r={port.name === mainPort ? 6 : 5}
                                fill={
                                    port.name === mainPort
                                        ? "#22d3ee"
                                        : shipPorts.includes(port.name)
                                            ? "#22c55e"
                                            : "#ef4444"
                                }
                            />
                        </>

                        <text
                            x={port.name === "London" ? -8 : 10}
                            y={3}
                            textAnchor={port.name === "London" ? "end" : "start"}
                            style={{
                                fontSize: "11px",
                                textShadow: "0 0 6px rgba(255,255,255,0.25)",
                                fill: "#e2e8f0",
                                fontWeight: "600",
                                letterSpacing: "0.3px"
                            }}
                        >
                            {port.name}
                        </text>

                    </Marker>
                ))}

                {hoveredPort && (
                    <Marker coordinates={[hoveredPort.longitude, hoveredPort.latitude]}>

                        <g transform={`translate(${offsetX}, ${offsetY})`}>

                            <rect
                                width={boxWidth}
                                height={90}
                                fill="#10284d"
                                stroke="#5fa8ff"
                                rx={2}
                            />

                            <text x={10} y={18} fill="#e2e8f0" fontSize="11" fontWeight="600">
                                Port Info
                            </text>

                            <line x1={10} y1={24} x2={boxWidth - 10} y2={24} stroke="#334155" />

                            <text x={10} y={40} fill="#cbd5f5" fontSize="10">
                                📦 {portCargo.length} cargos
                            </text>

                            <text x={10} y={55} fill="#cbd5f5" fontSize="10">
                                💰 {minReward === maxReward ? minReward : `${minReward} | ${maxReward}`}
                            </text>

                            <text x={10} y={70} fill={riskColor} fontSize="10">
                                ⚠️ {minRisk === maxRisk ? minRisk : `${minRisk} | ${maxRisk}`}
                            </text>

                        </g>

                    </Marker>
                )}

                {session.players.flatMap(p =>
                    (p.ships || []).map(ship => ({ ship, player: p }))
                ).map(({ ship, player }) => {

                    const voyage = voyages.find(
                        v => v.shipId === ship.id && v.status !== "FINISHED"
                    );

                    if (!voyage && ship.currentPort) {

                        const port = ports.find(pt => pt.name === ship.currentPort);
                        if (!port) return null;

                        return (
                            <Marker key={ship.id} coordinates={[port.longitude, port.latitude]}>

                                <>
                                    <image
                                        className="ship-icon"
                                        href={getShipImage(ship)}
                                        width={52}
                                        height={52}
                                        x={-26}
                                        y={-26}
                                    />

                                    {player.ships?.length > 0 && (
                                        <text
                                            y={24}
                                            textAnchor="middle"
                                            style={{
                                                fill: "#cfe8ff",
                                                fontSize: "11px",
                                                fontWeight: "600",
                                                textShadow: "0 0 4px rgba(0,0,0,0.8)"
                                            }}
                                        >
                                            {player.username}
                                        </text>
                                    )}
                                </>

                            </Marker>
                        );
                    }

                    if (voyage) {

                        const progress = Math.min(
                            1,
                            (smoothProgress[voyage.id] ?? (voyage.progress ?? 0) * 100) / 100
                        );

                        console.log("FRONTEND PROGRESS:", progress);

                        let position;

                        if (voyage.route && voyage.route.length > 1) {
                            position = getPositionOnRoute(voyage.route, progress);
                        } else {
                            const origin = ports.find(p => p.name === voyage.originPort);
                            const dest = ports.find(p => p.name === voyage.destinationPort);
                            if (!origin || !dest) return null;

                            const lat =
                                origin.latitude +
                                (dest.latitude - origin.latitude) * progress;

                            const lon =
                                origin.longitude +
                                (dest.longitude - origin.longitude) * progress;

                            position = [lon, lat];
                        }

                        return (
                            <Marker key={ship.id} coordinates={position}>

                                <>
                                    <image
                                        className="ship-icon"
                                        href={getShipImage(ship)}
                                        width={40}
                                        height={40}
                                        x={-20}
                                        y={-20}
                                    />

                                    <text
                                        y={20}
                                        textAnchor="middle"
                                        style={{
                                            fill: "#cfe8ff",
                                            fontSize: "10px",
                                            fontWeight: "600",
                                            textShadow: "0 0 4px rgba(0,0,0,0.8)"
                                        }}
                                    >
                                        {player.username}
                                    </text>
                                </>

                            </Marker>
                        );
                    }

                    return null;
                })}

            </ComposableMap>

        </div>
    );
}

export default GameMap;