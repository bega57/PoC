import "./VoyageEventModal.css";

export default function VoyageEventModal({ event, onSelect, loading }) {
    if (!event) {
        return null;
    }

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
                            className="voyage-event-option-button"
                            onClick={() => onSelect(index)}
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