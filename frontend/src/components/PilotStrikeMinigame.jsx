import { useEffect, useRef, useState } from "react";
import "./PilotStrikeMinigame.css";
import shipTopImg from "../assets/ships/cheapTop.png";

const SHIP_L    = 44;
const SHIP_W    = 20;
const THRUST    = 0.10;
const MAX_SPEED = 3.2;
const FRICTION  = 0.976;
const TURN_SPEED= 0.038;
const WIN_SPEED = 1.2;
const TIME_LIMIT= 60;
const DOCK_W    = 70;
const DOCK_H    = 110;
const WALL_T    = 14;

const shipImage = new Image();
shipImage.src = shipTopImg;

export default function PilotStrikeMinigame({ onResult }) {
    const canvasRef    = useRef(null);
    const containerRef = useRef(null);
    const animRef      = useRef(null);
    const timerRef     = useRef(null);
    const keysRef      = useRef({});
    const doneRef      = useRef(false);
    const onResultRef  = useRef(onResult);
    const resultWonRef = useRef(null);
    onResultRef.current = onResult;

    const gameRef = useRef(null);

    const [timeLeft,  setTimeLeft]  = useState(TIME_LIMIT);
    const [countdown, setCountdown] = useState(3);
    const [result,    setResult]    = useState(null);
    const [hint,      setHint]      = useState("");

    const finish = (won) => {
        if (doneRef.current) return;
        doneRef.current = true;
        if (animRef.current) cancelAnimationFrame(animRef.current);
        if (timerRef.current) clearInterval(timerRef.current);
        resultWonRef.current = won;
        setResult(won ? "win" : "lose");
    };

    useEffect(() => {
        containerRef.current?.focus();
    }, []);

    useEffect(() => {
        const down = (e) => {
            keysRef.current[e.key] = true;
            if (["ArrowUp","ArrowDown","ArrowLeft","ArrowRight"].includes(e.key)) {
                e.preventDefault();
                e.stopPropagation();
            }
        };
        const up = (e) => { keysRef.current[e.key] = false; };
        window.addEventListener("keydown", down, true);
        window.addEventListener("keyup",   up,   true);
        return () => {
            window.removeEventListener("keydown", down, true);
            window.removeEventListener("keyup",   up,   true);
        };
    }, []);

    useEffect(() => {
        if (countdown <= 0) return;
        const t = setTimeout(() => setCountdown(c => c - 1), 1000);
        return () => clearTimeout(t);
    }, [countdown]);

    useEffect(() => {
        if (countdown !== 0) return;

        const canvas = canvasRef.current;
        if (!canvas) return;

        const W = canvas.width  = window.innerWidth;
        const H = canvas.height = window.innerHeight;
        const ctx = canvas.getContext("2d");

        const dockX = Math.random() * (W - DOCK_W * 4) + DOCK_W * 2;
        const dock = { x: dockX, y: 0, w: DOCK_W, h: DOCK_H };

        const shipX = Math.random() * (W - 200) + 100;
        const randomAngle = (Math.random() - 0.5) * Math.PI;
        const ship = { x: shipX, y: H - 120, angle: -Math.PI / 2 + randomAngle, vx: 0, vy: 0 };

        const waves = Array.from({ length: 40 }, () => ({
            x: Math.random()*W, y: Math.random()*H,
            rx: Math.random()*55+20, ry: Math.random()*18+6,
            rot: Math.random()*Math.PI,
            speed: Math.random()*0.3+0.08, alpha: Math.random()*0.08+0.03,
        }));
        const trail = [];

        gameRef.current = { ship, dock, waves, trail, timeLeft: TIME_LIMIT };

        timerRef.current = setInterval(() => {
            if (doneRef.current) return;
            const g = gameRef.current;
            g.timeLeft--;
            setTimeLeft(g.timeLeft);
            if (g.timeLeft <= 0) finish(false);
        }, 1000);

        const drawWater = () => {
            const grad = ctx.createLinearGradient(0,0,0,H);
            grad.addColorStop(0,"#0a2744");
            grad.addColorStop(0.5,"#0d3a6b");
            grad.addColorStop(1,"#0a2744");
            ctx.fillStyle = grad;
            ctx.fillRect(0,0,W,H);
            ctx.strokeStyle = "rgba(255,255,255,0.025)";
            ctx.lineWidth = 1;
            for (let gx=0; gx<W; gx+=100) { ctx.beginPath(); ctx.moveTo(gx,0); ctx.lineTo(gx,H); ctx.stroke(); }
            for (let gy=0; gy<H; gy+=100) { ctx.beginPath(); ctx.moveTo(0,gy); ctx.lineTo(W,gy); ctx.stroke(); }
        };

        const drawWaves = () => {
            waves.forEach(w => {
                w.x += w.speed;
                if (w.x - w.rx > W) w.x = -w.rx;
                ctx.save(); ctx.translate(w.x, w.y); ctx.rotate(w.rot);
                ctx.beginPath(); ctx.ellipse(0,0,w.rx,w.ry,0,0,Math.PI*2);
                ctx.strokeStyle = `rgba(255,255,255,${w.alpha})`; ctx.lineWidth=1.5; ctx.stroke();
                ctx.restore();
            });
        };

        const drawDock = (shipInDock) => {
            const {x,y,w,h} = dock;
            const cx = x + w/2;

            // linke Wand
            ctx.fillStyle = "#5c3a1e";
            ctx.fillRect(x - WALL_T, y, WALL_T, h + 10);
            // rechte Wand
            ctx.fillRect(x + w, y, WALL_T, h + 10);
            // Rückwand (oben)
            ctx.fillRect(x - WALL_T, y, w + WALL_T * 2, WALL_T);

            // Holzplanken
            ctx.strokeStyle="#3d2410"; ctx.lineWidth=1;
            for (let py=y; py<y+h+10; py+=8) {
                ctx.beginPath(); ctx.moveTo(x-WALL_T,py); ctx.lineTo(x,py); ctx.stroke();
                ctx.beginPath(); ctx.moveTo(x+w,py); ctx.lineTo(x+w+WALL_T,py); ctx.stroke();
            }
            for (let py=y; py<y+WALL_T; py+=8) {
                ctx.beginPath(); ctx.moveTo(x-WALL_T,py); ctx.lineTo(x+w+WALL_T,py); ctx.stroke();
            }

            // Poller
            [y+30,y+70,y+110].forEach(by => {
                if (by > h) return;
                [[x-WALL_T+7,by],[x+w+WALL_T-7,by]].forEach(([bx,_by]) => {
                    ctx.beginPath(); ctx.arc(bx,_by,6,0,Math.PI*2);
                    ctx.fillStyle="#8b6914"; ctx.fill();
                    ctx.strokeStyle="#f59e0b"; ctx.lineWidth=2; ctx.stroke();
                });
            });

            // Dock-Wasser
            ctx.fillStyle="#071d38"; ctx.fillRect(x,y+WALL_T,w,h-WALL_T+10);

            // Glow
            const gg = ctx.createRadialGradient(cx,y+h/2,5,cx,y+h/2,70);
            gg.addColorStop(0, shipInDock ? "rgba(34,197,94,0.6)" : "rgba(34,197,94,0.25)");
            gg.addColorStop(1,"rgba(34,197,94,0)");
            ctx.fillStyle=gg; ctx.fillRect(x,y+WALL_T,w,h-WALL_T);

            // Gestrichelter Rahmen
            ctx.strokeStyle = shipInDock ? "#22c55e" : "rgba(34,197,94,0.5)";
            ctx.lineWidth = shipInDock ? 3 : 2;
            ctx.setLineDash([8,5]); ctx.strokeRect(x+2,y+WALL_T+2,w-4,h-WALL_T-4); ctx.setLineDash([]);

            // Label
            ctx.fillStyle = shipInDock ? "#22c55e" : "rgba(34,197,94,0.7)";
            ctx.font="bold 13px 'Courier New',monospace"; ctx.textAlign="center"; ctx.textBaseline="middle";
            ctx.fillText("DOCK HERE",cx,y+h/2);
        };

        const drawTrail = () => {
            trail.forEach((p,i) => {
                const alpha = (i/trail.length)*0.35;
                ctx.beginPath(); ctx.arc(p.x,p.y,p.r,0,Math.PI*2);
                ctx.fillStyle=`rgba(180,220,255,${alpha})`; ctx.fill();
                p.r *= 0.97;
            });
            while (trail.length > 60) trail.shift();
        };

        const drawShip = (s, thrusting) => {
            ctx.save();
            ctx.translate(s.x, s.y);
            ctx.rotate(s.angle - Math.PI / 2);

            const size = 48;
            ctx.drawImage(shipImage, -size / 2, -size / 2, size, size);

            // Triebwerk-Glow wenn Gas gegeben
            if (thrusting) {
                ctx.rotate(-Math.PI / 2); // zurückdrehen für den Glow
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

        const drawHUD = (spd) => {
            const spdPct = Math.min(spd/MAX_SPEED,1);
            const spdColor = spdPct>0.6 ? "#ef4444" : spdPct>0.3 ? "#f59e0b" : "#22c55e";
            const bx=24, by=H-90, bw=160, bh=12;
            ctx.fillStyle="rgba(0,0,0,0.55)"; ctx.fillRect(bx-10,by-28,bw+20,52);
            ctx.fillStyle="#94a3b8"; ctx.font="11px 'Courier New',monospace";
            ctx.textAlign="left"; ctx.textBaseline="middle"; ctx.fillText("SPEED",bx,by-14);
            ctx.fillStyle="#1e293b"; ctx.fillRect(bx,by,bw,bh);
            ctx.fillStyle=spdColor; ctx.fillRect(bx,by,bw*spdPct,bh);
            ctx.strokeStyle="#475569"; ctx.lineWidth=1; ctx.strokeRect(bx,by,bw,bh);
            const winX = bx+bw*(WIN_SPEED/MAX_SPEED);
            ctx.strokeStyle="#22c55e"; ctx.lineWidth=2;
            ctx.setLineDash([3,3]); ctx.beginPath(); ctx.moveTo(winX,by-4); ctx.lineTo(winX,by+bh+4); ctx.stroke(); ctx.setLineDash([]);
            ctx.fillStyle="#22c55e"; ctx.font="9px 'Courier New',monospace"; ctx.fillText("SAFE",winX+4,by+bh/2);
            ctx.font="11px 'Courier New',monospace"; ctx.fillStyle="rgba(255,255,255,0.4)";
            ctx.textAlign="center"; ctx.fillText("↑ Thrust  ←→ Steer  ↓ Brake",W/2,H-20);
        };

        let frameCount = 0;
        const loop = () => {
            if (doneRef.current) return;

            const s    = ship;
            const keys = keysRef.current;

            if (keys["ArrowLeft"])  s.angle -= TURN_SPEED;
            if (keys["ArrowRight"]) s.angle += TURN_SPEED;

            const thrusting = !!keys["ArrowUp"];
            if (thrusting) {
                s.vx += Math.cos(s.angle) * THRUST;
                s.vy += Math.sin(s.angle) * THRUST;
            }
            if (keys["ArrowDown"]) {
                s.vx -= Math.cos(s.angle) * THRUST * 0.55;
                s.vy -= Math.sin(s.angle) * THRUST * 0.55;
            }

            const spd = Math.sqrt(s.vx**2 + s.vy**2);
            if (spd > MAX_SPEED) { s.vx=(s.vx/spd)*MAX_SPEED; s.vy=(s.vy/spd)*MAX_SPEED; }

            s.vx *= FRICTION; s.vy *= FRICTION;
            s.x += s.vx; s.y += s.vy;

            // Bildschirmrand = sinken
            if (s.x < 0 || s.x > W || s.y < 0 || s.y > H) {
                finish(false);
                return;
            }

            // Dock-Wände = sinken
            const nearDock = s.x > dock.x - WALL_T - SHIP_W
                && s.x < dock.x + dock.w + WALL_T + SHIP_W
                && s.y < dock.y + dock.h;
            if (nearDock) {
                const hitLeft  = s.x < dock.x          && s.x > dock.x - WALL_T - SHIP_W;
                const hitRight = s.x > dock.x + dock.w && s.x < dock.x + dock.w + WALL_T + SHIP_W;
                const hitBack  = s.y < dock.y + WALL_T && s.x > dock.x - SHIP_W && s.x < dock.x + dock.w + SHIP_W;
                if (hitLeft || hitRight || hitBack) {
                    finish(false);
                    return;
                }
            }

            if (thrusting || spd > 0.3) trail.push({x:s.x, y:s.y, r:5+spd*1.5});

            const inDock = s.x > dock.x && s.x < dock.x + dock.w
                && s.y > dock.y + WALL_T && s.y < dock.y + dock.h;
            const curSpd = Math.sqrt(s.vx**2 + s.vy**2);

            frameCount++;
            if (frameCount % 10 === 0) {
                if (inDock && curSpd < WIN_SPEED) setHint("✅ Hold steady…");
                else if (inDock) setHint("⚠️ Too fast! Slow down!");
                else setHint("");
            }

            if (inDock && curSpd < WIN_SPEED) { finish(true); return; }

            drawWater(); drawWaves(); drawTrail();
            drawDock(inDock); drawShip(s, thrusting); drawHUD(curSpd);

            animRef.current = requestAnimationFrame(loop);
        };

        animRef.current = requestAnimationFrame(loop);

        const onResize = () => { canvas.width=window.innerWidth; canvas.height=window.innerHeight; };
        window.addEventListener("resize", onResize);

        return () => {
            cancelAnimationFrame(animRef.current);
            clearInterval(timerRef.current);
            window.removeEventListener("resize", onResize);
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [countdown === 0]);

    return (
        <div className="psm-fullscreen" tabIndex={0} ref={containerRef}>
            <canvas ref={canvasRef} className="psm-canvas" />

            <div className="psm-hud-top">
                <div className="psm-hud-title">PILOT STRIKE</div>
                <div className="psm-hud-subtitle">Dock your ship manually!</div>
            </div>

            <div className={`psm-timer ${timeLeft <= 10 ? "urgent" : ""}`}>
                {timeLeft}s
            </div>

            {hint && !result && <div className="psm-dock-hint">{hint}</div>}

            {countdown > 0 && (
                <div className="psm-countdown-overlay">
                    <div className="psm-countdown-box">
                        <div className="psm-countdown-label">Pilot Strike!</div>
                        <p>The harbor pilots are on strike.<br/>You must dock the ship yourself!</p>
                        <div className="psm-countdown-num">{countdown}</div>
                        <div className="psm-countdown-hint">Use arrow keys to steer</div>
                    </div>
                </div>
            )}

            {result && (
                <div className={`psm-result-overlay ${result}`}>
                    <div className="psm-result-box">
                        {result === "win" ? (<>
                            <div className="psm-result-title">Perfect Docking!</div>
                            <div className="psm-result-text">No damage, no cost. You are a natural captain!</div>
                        </>) : (<>
                            <div className="psm-result-icon">💥</div>
                            <div className="psm-result-title">Crash!</div>
                            <div className="psm-result-text">Hull damage: -40 condition. Should have bribed them.</div>
                        </>)}
                        <button
                            className="psm-ok-button"
                            onClick={() => onResultRef.current(resultWonRef.current)}
                        >
                            OK
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
