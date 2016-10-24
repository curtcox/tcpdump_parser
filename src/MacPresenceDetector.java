import java.util.function.Consumer;

final class MacPresenceDetector implements Consumer<Packet> {

    final Mac mac;
    final Listener listener;

    interface Listener {
        void onMacDetected(Mac mac, Packet packet);
    }

    MacPresenceDetector(Mac mac, Listener listener) {
        this.mac = mac;
        this.listener = listener;
    }

    @Override
    public void accept(Packet packet) {
        if (packet.contains(mac)) {
            listener.onMacDetected(mac,packet);
        }
    }
}
