final class MacDetectedEvent {

    final Mac mac;
    final Timestamp timestamp;
    final Packet current;
    final Packet previous;

    private MacDetectedEvent(Mac mac, Timestamp timestamp, Packet current, Packet previous) {
        this.mac = mac;
        this.timestamp = timestamp;
        this.current = current;
        this.previous = previous;
    }

    String time(Packet packet) {
        return packet==null ? "" : packet.localTime.toString();
    }

    @Override
    public String toString() {
        return String.format("%s %s current=%s previous=%s",mac,timestamp,time(current),time(previous));
    }

    public static MacDetectedEvent present(Mac mac, Packet current, Packet previousLastSeenPacketWithMAC) {
        return new MacDetectedEvent(mac,current.localTime,current,previousLastSeenPacketWithMAC);
    }

    public static MacDetectedEvent absent(Mac mac, Timestamp timestamp, Packet lastSeenPacketWithMAC) {
        return new MacDetectedEvent(mac,timestamp,null,lastSeenPacketWithMAC);
    }
}
