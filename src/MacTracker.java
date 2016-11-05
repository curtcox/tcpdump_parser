import java.util.function.*;

interface MacTracker extends Consumer<Packet> {
    interface Listener {
        void onNewMacAbsence(MacDetectedEvent event);
        void onNewMacPresence(MacDetectedEvent event);
        void onMacDetected(MacDetectedEvent event);
    }
}
