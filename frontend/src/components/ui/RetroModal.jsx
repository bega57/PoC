import "./RetroModal.css";

function RetroModal({ title, children, onClose }) {
    return (
        <div className="retro-overlay">
            <div className="retro-modal">

                <div className="retro-title">
                    {title}
                </div>

                <button className="retro-close" onClick={onClose}>
                    ✕
                </button>

                <div className="retro-content">
                    {children}
                </div>

            </div>
        </div>
    );
}

export default RetroModal;