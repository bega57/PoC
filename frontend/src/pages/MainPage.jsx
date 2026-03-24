import { useState } from "react";
import CreatePlayerForm from "../components/CreatePlayerForm";
import JoinSessionForm from "../components/JoinSessionForm";
import SessionDetails from "../components/SessionDetails";
import CreateSessionButton from "../components/CreateSessionButton";

function MainPage() {
    const [player, setPlayer] = useState(null);
    const [session, setSession] = useState(null);

    return (
        <div>
            <CreatePlayerForm onPlayerCreated={setPlayer} />
            <CreateSessionButton player={player} onSessionCreated={setSession} />
            <JoinSessionForm player={player} onSessionJoined={setSession} />
            <SessionDetails session={session} />
        </div>
    );
}

export default MainPage;