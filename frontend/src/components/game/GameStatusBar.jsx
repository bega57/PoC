import { useEffect, useRef, useState } from "react";

const TICK_RATE_MS = 5000;

function GameStatusBar({ myActiveVoyages, lastTickTimeRef }) {
    const [subTickFraction, setSubTickFraction] = useState(0);
    const internalRef = useRef(Date.now());
    const effectiveRef = lastTickTimeRef ?? internalRef;

    useEffect(() => {
        const interval = setInterval(() => {
            const elapsed = Date.now() - effectiveRef.current;
            setSubTickFraction(Math.min(1, elapsed / TICK_RATE_MS));
        }, 100);
        return () => clearInterval(interval);
    }, [effectiveRef]);

    return (
        <div className="status-bar">

            <div className="status-header">
                Fleet Status
                <span style={{ marginLeft: "6px", opacity: 0.6 }}>
                    ({myActiveVoyages.length})
                </span>
            </div>

            {myActiveVoyages.length > 0 ? (
                myActiveVoyages.map(v => {
                    const base = v.progress ?? 0;
                    const perTick = 1 / Math.max(1, v.duration);
                    const smoothProgress = Math.min(1, base + subTickFraction * perTick);
                    const smoothPercent = smoothProgress * 100;

                    const traveledDays = v.currentDay ?? 0;
                    const totalDays = v.duration ?? 1;

                    return (
                        <div key={v.id} style={{ marginTop: "8px" }}>

                            <p>🚢 Ship: {v.shipName || v.shipId}</p>

                            <p>
                                ⏳ Traveling {v.originPort} → {v.destinationPort}
                            </p>

                            <div style={{
                                width: "100%",
                                height: "10px",
                                background: "#1f2937",
                                borderRadius: "6px",
                                overflow: "hidden",
                                marginTop: "4px"
                            }}>
                                <div style={{
                                    width: `${smoothPercent}%`,
                                    height: "100%",
                                    background: "linear-gradient(90deg, #22c55e, #4ade80)",
                                    boxShadow: "0 0 6px #22c55e"
                                }} />
                            </div>

                            <p style={{ fontSize: "12px", opacity: 0.7 }}>
                                Day {traveledDays} / {totalDays}
                            </p>

                        </div>
                    );
                })
            ) : (
                <p style={{ marginTop: "8px", opacity: 0.6 }}>
                    No ships traveling
                </p>
            )}

        </div>
    );
}

export default GameStatusBar;
