final class MacPresenceEvent {

    final Mac mac;
    final Timestamp timestamp;
    final Packet current;
    final Packet previous;
    final boolean present;

    interface Listener {
        void onNewMacAbsence(MacPresenceEvent event);
        void onNewMacPresence(MacPresenceEvent event);
        void onMacDetected(MacPresenceEvent event);
    }

    private MacPresenceEvent(Mac mac, Timestamp timestamp, Packet current, Packet previous) {
        this.mac = mac;
        this.timestamp = timestamp;
        this.current = current;
        this.previous = previous;
        present = current != null;
    }

    String time(Packet packet) {
        return packet==null ? "" : packet.timestamp.toString();
    }

    @Override
    public String toString() {
        return String.format("%s %s current=%s previous=%s",mac,timestamp,time(current),time(previous));
    }

    public static MacPresenceEvent present(Mac mac, Packet current, Packet previousLastSeenPacketWithMAC) {
        return new MacPresenceEvent(mac,current.timestamp,current,previousLastSeenPacketWithMAC);
    }

    public static MacPresenceEvent absent(Mac mac, Timestamp timestamp, Packet lastSeenPacketWithMAC) {
        return new MacPresenceEvent(mac,timestamp,null,lastSeenPacketWithMAC);
    }
}
