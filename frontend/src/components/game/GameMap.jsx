import {
    ComposableMap,
    Geographies,
    Geography,
    Marker
} from "react-simple-maps";

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
                    ⚠️ Don't forget to pick a main port <br />
                    (You cannot change this later)
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
                                fill="#243447"
                                stroke="#1b2838"
                                style={{
                                    default: { fill: "#243447", outline: "none" },
                                    hover: { fill: "#243447", outline: "none" },
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
                                fill="#0f172a"
                                stroke="#475569"
                                rx={10}
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
                                        href={getShipImage(ship)}
                                        width={36}
                                        height={36}
                                        x={-18}
                                        y={-18}
                                    />

                                    {player.ships?.length > 0 && (
                                        <text
                                            y={16}
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

                        const progress = voyage.progress ?? 0;

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
                                        href={getShipImage(ship)}
                                        width={24}
                                        height={24}
                                        x={-12}
                                        y={-12}
                                    />

                                    <text
                                        y={16}
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