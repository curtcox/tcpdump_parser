final class SingleMacTracker implements MacTracker {

    private final Mac mac;
    private final MacTracker.Listener listener;
    private final GapDetector gapDetector;
    private Packet lastSeenPacketWithMAC;
    private boolean present;

    private SingleMacTracker(Mac mac, MacTracker.Listener listener, GapDetector gapDetector) {
        this.mac = mac;
        this.listener = listener;
        this.gapDetector = gapDetector;
    }

    static SingleMacTracker of(Mac mac, MacTracker.Listener listener) {
        return new SingleMacTracker(mac,listener, new DefaultGapDetector());
    }

    static SingleMacTracker of(Mac mac, MacTracker.Listener listener, GapDetector gapDetector) {
        return new SingleMacTracker(mac,listener, gapDetector);
    }

    @Override
    public void accept(Packet packet) {
        // This method was profiled and optimized, since replacing it with a NOP showed it accounts
        // for more than half of (typical?) execution time in running packets through a
        // MultipleMacTracker. Inlining the methods and caching results as done below, roughly
        // cuts the execution time of this method in half.
        if (packet.contains(mac)) {
            final MacDetectedEvent event = presenceEvent(packet);
            listener.onMacDetected(event);
            if (!present && lastSeenLongEnoughAgo(packet)) {
                present = true;
                listener.onNewMacPresence(event);
            }
            lastSeenPacketWithMAC = packet;
        } else {
            if (present && lastSeenPacketWithMAC != null && lastSeenLongEnoughAgo(packet)) {
                present = false;
                listener.onNewMacAbsence(absentEvent(packet.localTime));
            }
        }
    }

    boolean lastSeenLongEnoughAgo(Packet packet) {
        return gapDetector.isGapBetween(previousTime(),packet.localTime);
    }

    Timestamp previousTime() {
        return lastSeenPacketWithMAC == null ? null : lastSeenPacketWithMAC.localTime;
    }

    MacDetectedEvent presenceEvent(Packet current) {
        return MacDetectedEvent.present(mac,current, lastSeenPacketWithMAC);
    }

    MacDetectedEvent absentEvent(Timestamp timestamp) {
        return MacDetectedEvent.absent(mac,timestamp, lastSeenPacketWithMAC);
    }

}
