final class SingleMacPresenceTracker implements MacTracker {

    private final Mac mac;
    private final MacPresenceEvent.Listener listener;
    private final GapDetector gapDetector;
    private Packet lastSeenPacketWithMAC;
    private boolean present;

    private SingleMacPresenceTracker(Mac mac, MacPresenceEvent.Listener listener, GapDetector gapDetector) {
        this.mac = mac;
        this.listener = listener;
        this.gapDetector = gapDetector;
    }

    static SingleMacPresenceTracker of(Mac mac, MacPresenceEvent.Listener listener) {
        return new SingleMacPresenceTracker(mac,listener, DefaultGapDetector.minutes(3));
    }

    static SingleMacPresenceTracker of(Mac mac, MacPresenceEvent.Listener listener, GapDetector gapDetector) {
        return new SingleMacPresenceTracker(mac,listener, gapDetector);
    }

    @Override
    public void accept(Packet packet) {
        // This method was profiled and optimized, since replacing it with a NOP showed it accounts
        // for more than half of (typical?) execution signal in running packets through a
        // MultipleMacPresenceTracker. Inlining the methods and caching results as done below, roughly
        // cuts the execution signal of this method in half.
        if (packet.contains(mac)) {
            final MacPresenceEvent event = presentEvent(packet);
            listener.onMacDetected(event);
            if (!present && lastSeenLongEnoughAgo(packet)) {
                present = true;
                listener.onNewMacPresence(event);
            }
            lastSeenPacketWithMAC = packet;
        } else {
            if (present && lastSeenPacketWithMAC != null && lastSeenLongEnoughAgo(packet)) {
                present = false;
                listener.onNewMacAbsence(absentEvent(packet.timestamp));
            }
        }
    }

    boolean lastSeenLongEnoughAgo(Packet packet) {
        return gapDetector.isGapBetween(previousTime(),packet.timestamp);
    }

    Timestamp previousTime() {
        return lastSeenPacketWithMAC == null ? null : lastSeenPacketWithMAC.timestamp;
    }

    MacPresenceEvent presentEvent(Packet current) {
        return MacPresenceEvent.present(mac,current, lastSeenPacketWithMAC);
    }

    MacPresenceEvent absentEvent(Timestamp timestamp) {
        return MacPresenceEvent.absent(mac,timestamp, lastSeenPacketWithMAC);
    }

    public static void main(String[] args) {
        Mac mac = args.length < 1 ? Mac.all0 : Mac.of(args[0]);
        MacPresenceEvent.Listener listener = MacPresenceChangeAction.of(e->{System.out.println(e);});
        Main.forEachReliablePacket(SingleMacPresenceTracker.of(mac,listener));
    }
}
