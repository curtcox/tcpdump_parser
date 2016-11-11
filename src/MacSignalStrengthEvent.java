import java.util.function.Consumer;

final class MacSignalStrengthEvent {

    final Mac mac;
    final Timestamp timestamp;
    final Packet current;
    final Packet previous;
    final boolean present;

    interface Listener extends Consumer<MacSignalStrengthEvent> {}

    private MacSignalStrengthEvent(Mac mac, Timestamp timestamp, Packet current, Packet previous) {
        this.mac = mac;
        this.timestamp = timestamp;
        this.current = current;
        this.previous = previous;
        present = current != null;
    }

    String signal(Packet packet) {
        return packet==null ? "" : packet.signal + "/" + packet.noise;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s -> %s %s",mac,timestamp, signal(previous), signal(current), bar(current));
    }

    private String bar(Packet current) {
        StringBuilder out = new StringBuilder();
        for (int i=0; i< strength(current); i++) {
            out.append("*");
        }
        return out.toString();
    }

    int strength(Packet current) {
        return current.signal.value - current.noise.value;
    }

    public static MacSignalStrengthEvent of(Mac mac, Packet current, Packet previousLastSeenPacketWithMAC) {
        return new MacSignalStrengthEvent(mac,current.timestamp,current,previousLastSeenPacketWithMAC);
    }

}
