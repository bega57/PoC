import { useState } from "react";
import "./VoyageEventModal.css";
import PilotStrikeMinigame from "./PilotStrikeMinigame";

export default function VoyageEventModal({ event, onSelect, loading }) {
    const [showMinigame, setShowMinigame] = useState(false);

    if (!event) return null;

    if (showMinigame) {
        return (
            <PilotStrikeMinigame
                onResult={(won) => {
                    setShowMinigame(false);
                    if (won) {
                        onSelect(1);
                    } else {
                        onSelect(2);
                    }
                }}
            />
        );
    }

    const handleOptionClick = (index) => {
        const option = event.options[index];
        if (option?.minigame) {
            setShowMinigame(true);
            return;
        }
        onSelect(index);
    };

    return (
        <div className="voyage-event-overlay">
            <div className="voyage-event-modal">
                <div className="voyage-event-icon">⚠️</div>

                <h2>{event.title}</h2>

                <p className="voyage-event-description">
                    {event.description}
                </p>

                <div className="voyage-event-options">
                    {event.options.map((option, index) => (
                        <button
                            key={option.label}
                            className={`voyage-event-option-button ${option.minigame ? "minigame-option" : ""}`}
                            onClick={() => handleOptionClick(index)}
                            disabled={loading}
                        >
                            <span className="voyage-event-option-label">
                                {option.label}
                            </span>
                            <span className="voyage-event-option-consequence">
                                {option.consequence}
                            </span>
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
}