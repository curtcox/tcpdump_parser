import java.util.function.Consumer;

final class MacTracker implements Consumer<Packet> {

    private final Mac mac;
    private final Listener listener;

    interface Listener {
        void onMacDetected(MacDetectedEvent event);
    }

    MacTracker(Mac mac, Listener listener) {
        this.mac = mac;
        this.listener = listener;
    }

    @Override
    public void accept(Packet packet) {
        if (packet.contains(mac)) {
            listener.onMacDetected(new MacDetectedEvent(mac,packet));
        }
    }
}
