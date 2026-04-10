import {
    ComposableMap,
    Geographies,
    Geography,
    Marker
} from "react-simple-maps";

function GameMap({
                     session,
                     ports,
                     voyages,
                     hoveredPort,
                     setHoveredPort,
                     handlePortHover,
                     showWelcome,
                     showPortInstruction,
                     setSelectedPort,
                     currentPort,
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
                            if (!showWelcome && !showPortInstruction) {
                                setSelectedPort(port.name);
                            }
                        }}
                    >

                        <>
                            {port.name === currentPort && (
                                <circle
                                    r={12}
                                    fill="rgba(34,211,238,0.2)"
                                />
                            )}

                            <circle
                                r={port.name === currentPort ? 6 : 5}
                                fill={
                                    port.name === currentPort
                                        ? "#22d3ee"
                                        : voyages.some(
                                            v =>
                                                myShipIds.includes(v.shipId) &&
                                                v.destinationPort === port.name &&
                                                v.status === "FINISHED"
                                        )
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

                        const origin = ports.find(p => p.name === voyage.originPort);
                        const dest = ports.find(p => p.name === voyage.destinationPort);
                        if (!origin || !dest) return null;

                        const start = new Date(voyage.startTime).getTime();
                        const now = Date.now();

                        let progress = 0;

                        if (!voyage.arrivalTime) {
                            progress = Math.min((now - start) / 20000, 1);
                        } else {
                            const end = new Date(voyage.arrivalTime).getTime();
                            progress = Math.min((now - start) / (end - start), 1);
                        }

                        const lat =
                            origin.latitude +
                            (dest.latitude - origin.latitude) * progress;

                        const lon =
                            origin.longitude +
                            (dest.longitude - origin.longitude) * progress;

                        return (
                            <Marker key={ship.id} coordinates={[lon, lat]}>

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