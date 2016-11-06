final class MacDetectedEvent {
    final Mac mac;
    final Packet current;
    final Packet previous;

    MacDetectedEvent(Mac mac, Packet current, Packet previous) {
        this.mac = mac;
        this.current = current;
        this.previous = previous;
    }

    String time(Packet packet) {
        return packet==null ? "" : packet.localTime.toString();
    }

    @Override
    public String toString() {
        return String.format("%s current=%s previous=%s",mac,time(current),time(previous));
    }
}
