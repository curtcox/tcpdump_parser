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
        checkMacDetected(packet);
        checkNewMacPresence(packet);
        checkNewMacAbsence(packet);
        updateLastSeen(packet);
    }

    boolean matching(Packet packet) {
        return packet.contains(mac);
    }

    void checkNewMacPresence(Packet packet) {
        if (!present && matching(packet) && lastSeenLongEnoughAgo(packet)) {
            present = true;
            listener.onNewMacPresence(detectedEvent(packet));
        }
    }

    void checkNewMacAbsence(Packet packet) {
        if (present && !matching(packet) && lastSeenPacketWithMAC != null && lastSeenLongEnoughAgo(packet)) {
            present = false;
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
            lastSeenPacketWithMAC = packet;
        }
    }

    boolean lastSeenLongEnoughAgo(Packet packet) {
        return gapDetector.isGapBetween(previousTime(),packet.localTime);
    }

    Timestamp previousTime() {
        return lastSeenPacketWithMAC == null ? null : lastSeenPacketWithMAC.localTime;
    }

    MacDetectedEvent detectedEvent(Packet current) {
        return new MacDetectedEvent(mac,current, lastSeenPacketWithMAC);
    }
}
