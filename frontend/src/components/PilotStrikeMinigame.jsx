import { useEffect, useRef, useState, useCallback } from "react";
import "./PilotStrikeMinigame.css";

const SHIP_L        = 44;
const SHIP_W        = 20;
const THRUST        = 0.10;
const MAX_SPEED     = 3.2;
const FRICTION      = 0.976;
const TURN_SPEED    = 0.038;
const WIN_SPEED     = 1.2;
const TIME_LIMIT    = 60;
const DOCK_W        = 90;
const DOCK_H        = 130;
const WALL_T        = 14;

export default function PilotStrikeMinigame({ onResult }) {
    const canvasRef    = useRef(null);
    const containerRef = useRef(null);
    const stateRef     = useRef(null);
    const animRef      = useRef(null);
    const timerRef     = useRef(null);
    const keysRef      = useRef({});
    const doneRef      = useRef(false);

    const [timeLeft,   setTimeLeft]   = useState(TIME_LIMIT);
    const [countdown,  setCountdown]  = useState(3);
    const [result,     setResult]     = useState(null);
    const [speed,      setSpeed]      = useState(0);
    const [inDockZone, setInDockZone] = useState(false);

    const finish = useCallback((won) => {
        if (doneRef.current) return;
        doneRef.current = true;
        cancelAnimationFrame(animRef.current);
        clearInterval(timerRef.current);
        setResult(won ? "win" : "lose");
        setTimeout(() => onResult(won), 2200);
    }, [onResult]);

    // Fokus setzen damit Pfeiltasten funktionieren
    useEffect(() => {
        containerRef.current?.focus();
    }, []);

    // Keyboard mit capture:true damit preventDefault greift
    useEffect(() => {
        const down = (e) => {
            keysRef.current[e.key] = true;
            if (["ArrowUp","ArrowDown","ArrowLeft","ArrowRight"].includes(e.key)) {
                e.preventDefault();
                e.stopPropagation();
            }
        };
        const up = (e) => { keysRef.current[e.key] = false; };
        window.addEventListener("keydown", down, { capture: true });
        window.addEventListener("keyup",   up,   { capture: true });
        return () => {
            window.removeEventListener("keydown", down, { capture: true });
            window.removeEventListener("keyup",   up,   { capture: true });
        };
    }, []);

    useEffect(() => {
        if (countdown <= 0) return;
        const t = setTimeout(() => setCountdown(c => c - 1), 1000);
        return () => clearTimeout(t);
    }, [countdown]);

    useEffect(() => {

        const canvas = canvasRef.current;
        if (!canvas) return;

        const W = canvas.width  = window.innerWidth;
        const H = canvas.height = window.innerHeight;
        const ctx = canvas.getContext("2d");

        const dock = {
            x: W / 2 - DOCK_W / 2,
            y: 0,
            w: DOCK_W,
            h: DOCK_H,
        };

        const ship = {
            x:     W / 2,
            y:     H - 120,
            angle: -Math.PI / 2,
            vx:    0,
            vy:    0,
        };

        const waves = Array.from({ length: 40 }, () => ({
            x:     Math.random() * W,
            y:     Math.random() * H,
            rx:    Math.random() * 55 + 20,
            ry:    Math.random() * 18 + 6,
            rot:   Math.random() * Math.PI,
            speed: Math.random() * 0.3 + 0.08,
            alpha: Math.random() * 0.08 + 0.03,
        }));

        const trail = [];
        stateRef.current = { ship, dock, waves, trail };

        timerRef.current = setInterval(() => {
            setTimeLeft(t => {
                const next = t - 1;
                if (next <= 0) finish(false);
                return next;
            });
        }, 1000);

        const drawWater = () => {
            const grad = ctx.createLinearGradient(0, 0, 0, H);
            grad.addColorStop(0,   "#0a2744");
            grad.addColorStop(0.5, "#0d3a6b");
            grad.addColorStop(1,   "#0a2744");
            ctx.fillStyle = grad;
            ctx.fillRect(0, 0, W, H);

            ctx.strokeStyle = "rgba(255,255,255,0.025)";
            ctx.lineWidth = 1;
            for (let gx = 0; gx < W; gx += 100) {
                ctx.beginPath(); ctx.moveTo(gx, 0); ctx.lineTo(gx, H); ctx.stroke();
            }
            for (let gy = 0; gy < H; gy += 100) {
                ctx.beginPath(); ctx.moveTo(0, gy); ctx.lineTo(W, gy); ctx.stroke();
            }
        };

        const drawWaves = () => {
            waves.forEach(w => {
                w.x += w.speed;
                if (w.x - w.rx > W) w.x = -w.rx;
                ctx.save();
                ctx.translate(w.x, w.y);
                ctx.rotate(w.rot);
                ctx.beginPath();
                ctx.ellipse(0, 0, w.rx, w.ry, 0, 0, Math.PI * 2);
                ctx.strokeStyle = `rgba(255,255,255,${w.alpha})`;
                ctx.lineWidth = 1.5;
                ctx.stroke();
                ctx.restore();
            });
        };

        const drawDock = () => {
            const { x, y, w, h } = dock;
            const cx = x + w / 2;

            ctx.fillStyle = "#5c3a1e";
            ctx.fillRect(x - WALL_T, y, WALL_T, h + 10);
            ctx.fillRect(x + w, y, WALL_T, h + 10);
            ctx.fillRect(x - WALL_T, y, w + WALL_T * 2, WALL_T);

            ctx.strokeStyle = "#3d2410";
            ctx.lineWidth = 1;
            for (let py = y; py < y + h + 10; py += 8) {
                ctx.beginPath(); ctx.moveTo(x - WALL_T, py); ctx.lineTo(x, py); ctx.stroke();
                ctx.beginPath(); ctx.moveTo(x + w, py); ctx.lineTo(x + w + WALL_T, py); ctx.stroke();
            }
            for (let py = y; py < y + WALL_T; py += 8) {
                ctx.beginPath(); ctx.moveTo(x - WALL_T, py); ctx.lineTo(x + w + WALL_T, py); ctx.stroke();
            }

            const bollardsY = [y + 30, y + 70, y + 110];
            bollardsY.forEach(by => {
                if (by > h) return;
                [[x - WALL_T + 7, by], [x + w + WALL_T - 7, by]].forEach(([bx, _by]) => {
                    ctx.beginPath();
                    ctx.arc(bx, _by, 6, 0, Math.PI * 2);
                    ctx.fillStyle = "#8b6914";
                    ctx.fill();
                    ctx.strokeStyle = "#f59e0b";
                    ctx.lineWidth = 2;
                    ctx.stroke();
                });
            });

            ctx.fillStyle = "#071d38";
            ctx.fillRect(x, y + WALL_T, w, h - WALL_T + 10);

            const s = stateRef.current?.ship;
            const shipInDock = s && s.x > x && s.x < x + w && s.y > y + WALL_T && s.y < y + h;

            const glowGrad = ctx.createRadialGradient(cx, y + h / 2, 5, cx, y + h / 2, 70);
            if (shipInDock) {
                glowGrad.addColorStop(0, "rgba(34,197,94,0.6)");
                glowGrad.addColorStop(1, "rgba(34,197,94,0)");
            } else {
                glowGrad.addColorStop(0, "rgba(34,197,94,0.25)");
                glowGrad.addColorStop(1, "rgba(34,197,94,0)");
            }
            ctx.fillStyle = glowGrad;
            ctx.fillRect(x, y + WALL_T, w, h - WALL_T);

            ctx.strokeStyle = shipInDock ? "#22c55e" : "rgba(34,197,94,0.5)";
            ctx.lineWidth   = shipInDock ? 3 : 2;
            ctx.setLineDash([8, 5]);
            ctx.strokeRect(x + 2, y + WALL_T + 2, w - 4, h - WALL_T - 4);
            ctx.setLineDash([]);

            ctx.fillStyle    = shipInDock ? "#22c55e" : "rgba(34,197,94,0.7)";
            ctx.font         = "bold 13px 'Courier New', monospace";
            ctx.textAlign    = "center";
            ctx.textBaseline = "middle";
            ctx.fillText("⚓  DOCK HERE", cx, y + h / 2);
        };

        const drawTrail = () => {
            trail.forEach((p, i) => {
                const alpha = (i / trail.length) * 0.35;
                ctx.beginPath();
                ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
                ctx.fillStyle = `rgba(180,220,255,${alpha})`;
                ctx.fill();
                p.r *= 0.97;
            });
            while (trail.length > 60) trail.shift();
        };

        const drawShip = (s, thrusting) => {
            ctx.save();
            ctx.translate(s.x, s.y);
            ctx.rotate(s.angle);

            if (thrusting) {
                const wake = ctx.createLinearGradient(-SHIP_L, 0, -SHIP_L - 30, 0);
                wake.addColorStop(0, "rgba(200,230,255,0.4)");
                wake.addColorStop(1, "rgba(200,230,255,0)");
                ctx.beginPath();
                ctx.moveTo(-SHIP_L / 2, -SHIP_W / 2 - 4);
                ctx.lineTo(-SHIP_L / 2 - 28, -18);
                ctx.lineTo(-SHIP_L / 2 - 28, 18);
                ctx.lineTo(-SHIP_L / 2, SHIP_W / 2 + 4);
                ctx.fillStyle = wake;
                ctx.fill();
            }

            ctx.beginPath();
            ctx.moveTo(SHIP_L / 2,       0);
            ctx.lineTo(SHIP_L / 4,      -SHIP_W / 2);
            ctx.lineTo(-SHIP_L / 2 + 8, -SHIP_W / 2);
            ctx.lineTo(-SHIP_L / 2,     -SHIP_W / 2 + 5);
            ctx.lineTo(-SHIP_L / 2,      SHIP_W / 2 - 5);
            ctx.lineTo(-SHIP_L / 2 + 8,  SHIP_W / 2);
            ctx.lineTo(SHIP_L / 4,       SHIP_W / 2);
            ctx.closePath();
            ctx.fillStyle   = "#d1d5db";
            ctx.fill();
            ctx.strokeStyle = "#6b7280";
            ctx.lineWidth   = 1.5;
            ctx.stroke();

            ctx.fillStyle = "#4b5563";
            ctx.fillRect(-14, -SHIP_W / 2 + 3, 28, SHIP_W - 6);

            ctx.fillStyle = "#1e293b";
            ctx.fillRect(-6, -5, 14, 10);

            ctx.fillStyle    = "rgba(255,255,255,0.5)";
            ctx.font         = "9px serif";
            ctx.textAlign    = "center";
            ctx.textBaseline = "middle";
            ctx.fillText("⚓", SHIP_L / 4 - 2, 0);

            if (thrusting) {
                const r = 5 + Math.random() * 4;
                const glow = ctx.createRadialGradient(-SHIP_L / 2, 0, 0, -SHIP_L / 2, 0, r * 2);
                glow.addColorStop(0, "rgba(251,191,36,0.9)");
                glow.addColorStop(1, "rgba(251,191,36,0)");
                ctx.fillStyle = glow;
                ctx.beginPath();
                ctx.arc(-SHIP_L / 2, 0, r * 2, 0, Math.PI * 2);
                ctx.fill();
            }

            ctx.restore();
        };

        const drawHUD = (s) => {
            const spd = Math.sqrt(s.vx ** 2 + s.vy ** 2);
            const spdPct = Math.min(spd / MAX_SPEED, 1);
            const spdColor = spdPct > 0.6 ? "#ef4444" : spdPct > 0.3 ? "#f59e0b" : "#22c55e";

            const bx = 24, by = H - 90, bw = 160, bh = 12;
            ctx.fillStyle = "rgba(0,0,0,0.55)";
            ctx.fillRect(bx - 10, by - 28, bw + 20, 52);

            ctx.fillStyle    = "#94a3b8";
            ctx.font         = "11px 'Courier New', monospace";
            ctx.textAlign    = "left";
            ctx.textBaseline = "middle";
            ctx.fillText("SPEED", bx, by - 14);

            ctx.fillStyle = "#1e293b";
            ctx.fillRect(bx, by, bw, bh);
            ctx.fillStyle = spdColor;
            ctx.fillRect(bx, by, bw * spdPct, bh);
            ctx.strokeStyle = "#475569";
            ctx.lineWidth = 1;
            ctx.strokeRect(bx, by, bw, bh);

            const winX = bx + bw * (WIN_SPEED / MAX_SPEED);
            ctx.strokeStyle = "#22c55e";
            ctx.lineWidth = 2;
            ctx.setLineDash([3, 3]);
            ctx.beginPath(); ctx.moveTo(winX, by - 4); ctx.lineTo(winX, by + bh + 4); ctx.stroke();
            ctx.setLineDash([]);

            ctx.fillStyle = "#22c55e";
            ctx.font      = "9px 'Courier New', monospace";
            ctx.fillText("SAFE", winX + 4, by + bh / 2);

            ctx.font      = "11px 'Courier New', monospace";
            ctx.fillStyle = "rgba(255,255,255,0.4)";
            ctx.textAlign = "center";
            ctx.fillText("↑ Thrust  ←→ Steer  ↓ Brake", W / 2, H - 20);
        };

        const loop = () => {
            if (doneRef.current) return;

            const s    = stateRef.current.ship;
            const keys = keysRef.current;

            if (keys["ArrowLeft"])  s.angle -= TURN_SPEED;
            if (keys["ArrowRight"]) s.angle += TURN_SPEED;

            const thrusting = keys["ArrowUp"];
            if (thrusting) {
                s.vx += Math.cos(s.angle) * THRUST;
                s.vy += Math.sin(s.angle) * THRUST;
            }
            if (keys["ArrowDown"]) {
                s.vx -= Math.cos(s.angle) * THRUST * 0.55;
                s.vy -= Math.sin(s.angle) * THRUST * 0.55;
            }

            const spd = Math.sqrt(s.vx ** 2 + s.vy ** 2);
            if (spd > MAX_SPEED) {
                s.vx = (s.vx / spd) * MAX_SPEED;
                s.vy = (s.vy / spd) * MAX_SPEED;
            }

            s.vx *= FRICTION;
            s.vy *= FRICTION;
            s.x  += s.vx;
            s.y  += s.vy;

            if (s.x < SHIP_W)     { s.x = SHIP_W;     s.vx =  Math.abs(s.vx) * 0.4; }
            if (s.x > W - SHIP_W) { s.x = W - SHIP_W; s.vx = -Math.abs(s.vx) * 0.4; }
            if (s.y < SHIP_W)     { s.y = SHIP_W;     s.vy =  Math.abs(s.vy) * 0.4; }
            if (s.y > H - SHIP_W) { s.y = H - SHIP_W; s.vy = -Math.abs(s.vy) * 0.4; }

            if (thrusting || spd > 0.3) {
                trail.push({ x: s.x, y: s.y, r: 5 + spd * 1.5 });
            }

            const { x: dx, y: dy, w: dw, h: dh } = dock;
            const inDock = s.x > dx && s.x < dx + dw && s.y > dy + WALL_T && s.y < dy + dh;
            const curSpd = Math.sqrt(s.vx ** 2 + s.vy ** 2);

            setSpeed(curSpd);
            setInDockZone(inDock);

            if (inDock && curSpd < WIN_SPEED) {
                finish(true);
                return;
            }

            drawWater();
            drawWaves();
            drawTrail();
            drawDock();
            drawShip(s, thrusting);
            drawHUD(s);

            animRef.current = requestAnimationFrame(loop);
        };

        animRef.current = requestAnimationFrame(loop);

        const onResize = () => {
            canvas.width  = window.innerWidth;
            canvas.height = window.innerHeight;
        };
        window.addEventListener("resize", onResize);

        return () => {
            cancelAnimationFrame(animRef.current);
            clearInterval(timerRef.current);
            window.removeEventListener("resize", onResize);
        };
    }, [finish]);

    return (
        <div className="psm-fullscreen" tabIndex={0} ref={containerRef}>
            <canvas ref={canvasRef} className="psm-canvas" />

            <div className="psm-hud-top">
                <div className="psm-hud-title">⚓ PILOT STRIKE</div>
                <div className="psm-hud-subtitle">Dock your ship manually!</div>
            </div>

            <div className={`psm-timer ${timeLeft <= 10 ? "urgent" : ""}`}>
                {timeLeft}s
            </div>

            {inDockZone && !result && (
                <div className="psm-dock-hint">
                    {speed < WIN_SPEED
                        ? "✅ Hold steady…"
                        : "⚠️ Too fast! Slow down!"}
                </div>
            )}

            {countdown > 0 && (
                <div className="psm-countdown-overlay">
                    <div className="psm-countdown-box">
                        <div className="psm-countdown-label">⚓ Pilot Strike!</div>
                        <p>The harbor pilots are on strike.<br />You must dock the ship yourself!</p>
                        <div className="psm-countdown-num">{countdown}</div>
                        <div className="psm-countdown-hint">Use arrow keys to steer</div>
                    </div>
                </div>
            )}

            {result && (
                <div className={`psm-result-overlay ${result}`}>
                    <div className="psm-result-box">
                        {result === "win" ? (
                            <>
                                <div className="psm-result-icon">✅</div>
                                <div className="psm-result-title">Perfect Docking!</div>
                                <div className="psm-result-text">No damage, no cost. You're a natural captain!</div>
                            </>
                        ) : (
                            <>
                                <div className="psm-result-icon">💥</div>
                                <div className="psm-result-title">You Missed!</div>
                                <div className="psm-result-text">Hull damage: -40 condition. Should've bribed them.</div>
                            </>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}