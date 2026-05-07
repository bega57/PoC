import "../styles/background.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./InfoPage.css";

const slides = [
    {
        title: "Your first ship 🚢",
        text: "Every captain starts with one vessel.",
        image: "/ship.png"
    },
    {
        title: "Take cargo 📦",
        text: "Transport goods to earn money.",
        image: "/cargo.png"
    },
    {
        title: "Manage your fleet 🔧",
        text: "Repair and refuel your ships.",
        image: "/fleet.png"
    },
    {
        title: "Ready to sail 🌊",
        text: "Your journey begins now.",
        image: "/ready.png"
    }
];

function InfoPage() {
    const [index, setIndex] = useState(0);
    const navigate = useNavigate();

    const next = () => {
        if (index < slides.length - 1) {
            setIndex(index + 1);
        } else {
            navigate("/main");
        }
    };
    
    const slide = slides[index];

    return (
        <div className="ocean-bg">

            <div className="ships-bg">
                <div className="ship ship1"></div>
                <div className="ship ship2"></div>
            </div>

            <div className="tutorial-card">

                <img
                    src={slide.image}
                    className="tutorial-image"
                    alt=""
                />

                <h1>{slide.title}</h1>
                <p>{slide.text}</p>

                {/* DOTS */}
                <div className="dots">
                    {slides.map((_, i) => (
                        <div
                            key={i}
                            className={i === index ? "dot active" : "dot"}
                        />
                    ))}
                </div>

                <button onClick={next}>
                    {index === slides.length - 1
                        ? "Let's start!"
                        : "Next"}
                </button>

            </div>
        </div>
    );
}

export default InfoPage;