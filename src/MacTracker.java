import java.time.LocalTime;
import java.util.function.Consumer;

final class MacTracker implements Consumer<Packet> {

    private final Mac mac;
    private final Listener listener;
    private final GapDetector gapDetector;
    private Packet previous;

    interface Listener {
        void onNewMacAbsence(MacDetectedEvent event);
        void onNewMacPresence(MacDetectedEvent event);
        void onMacDetected(MacDetectedEvent event);
    }

    private MacTracker(Mac mac, Listener listener, GapDetector gapDetector) {
        this.mac = mac;
        this.listener = listener;
        this.gapDetector = gapDetector;
    }

    static MacTracker of(Mac mac, Listener listener) {
        return new MacTracker(mac,listener, new DefaultGapDetector());
    }

    static MacTracker of(Mac mac, Listener listener, GapDetector gapDetector) {
        return new MacTracker(mac,listener, gapDetector);
    }

    @Override
    public void accept(Packet packet) {
        if (packet.contains(mac)) {
            listener.onMacDetected(detectedEvent(packet));
            if (gapDetector.isGapBetween(previousTime(),packet.localTime)) {
                listener.onNewMacPresence(detectedEvent(packet));
            }
            previous = packet;
        } else {
            if (gapDetector.isGapBetween(previousTime(),packet.localTime)) {
                listener.onNewMacAbsence(detectedEvent(null));
            }
        }
    }

    LocalTime previousTime() {
        return previous == null ? null : previous.localTime;
    }

    MacDetectedEvent detectedEvent(Packet current) {
        return new MacDetectedEvent(mac,current,previous);
    }
}
