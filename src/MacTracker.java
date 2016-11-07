import java.util.function.*;

interface MacTracker extends Consumer<Packet> {
    interface Listener {
        void onNewMacAbsence(MacPresenceEvent event);
        void onNewMacPresence(MacPresenceEvent event);
        void onMacDetected(MacPresenceEvent event);
    }
}
