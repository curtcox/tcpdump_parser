final class MacDetectedEvent {
    final Mac mac;
    final Packet packet;
    MacDetectedEvent(Mac mac, Packet packet) {
        this.mac = mac;
        this.packet = packet;
    }
}
