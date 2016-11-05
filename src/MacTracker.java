import java.util.function.*;

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
        checkMacDetected(packet);
        checkNewMacPresence(packet);
        checkNewMacAbsence(packet);
        updateLastSeen(packet);
    }

    boolean matching(Packet packet) {
        return packet.contains(mac);
    }

    void checkNewMacPresence(Packet packet) {
        if (matching(packet) && lastSeenLongEnoughAgo(packet)) {
            listener.onNewMacPresence(detectedEvent(packet));
        }
    }

    void checkNewMacAbsence(Packet packet) {
        if (!matching(packet) && previous != null && lastSeenLongEnoughAgo(packet)) {
            listener.onNewMacAbsence(detectedEvent(null));
        }
    }

    void checkMacDetected(Packet packet) {
        if (matching(packet)) {
            listener.onMacDetected(detectedEvent(packet));
        }
    }

    void updateLastSeen(Packet packet) {
        if (matching(packet)) {
            previous = packet;
        }
    }

    boolean lastSeenLongEnoughAgo(Packet packet) {
        return gapDetector.isGapBetween(previousTime(),packet.localTime);
    }

    Timestamp previousTime() {
        return previous == null ? null : previous.localTime;
    }

    MacDetectedEvent detectedEvent(Packet current) {
        return new MacDetectedEvent(mac,current,previous);
    }
}
