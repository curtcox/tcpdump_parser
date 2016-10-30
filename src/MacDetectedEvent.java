final class MacDetectedEvent {
    final Mac mac;
    final Packet current;
    final Packet previous;

    MacDetectedEvent(Mac mac, Packet current, Packet previous) {
        this.mac = mac;
        this.current = current;
        this.previous = previous;
    }
}
