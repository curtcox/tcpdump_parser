import org.junit.Test;

import static org.junit.Assert.*;

public class MacTrackerTest {

    Mac mac = Mac.of("01:02:03:04:05:06");
    Listener listener = new Listener();
    MacTracker detector = new MacTracker(mac,listener);

    static class Listener implements MacTracker.Listener {

        boolean triggered;
        Mac mac;
        Packet packet;

        @Override
        public void onMacDetected(MacDetectedEvent event) {
            triggered = true;
            this.mac = event.mac;
            this.packet = event.packet;
        }
    }

    @Test
    public void listener_not_triggered_when_no_packets_examined() {
        assertFalse(listener.triggered);
    }

    @Test
    public void listener_not_triggered_by_packet_without_MAC() {
        Packet packet = Packet.builder().build();
        detector.accept(packet);
        assertFalse(listener.triggered);
    }

    @Test
    public void listener_triggered_by_packet_with_MAC() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        Packet packet = builder.build();
        detector.accept(packet);
        assert(listener.triggered);
        assertSame(mac,listener.mac);
        assertSame(packet,listener.packet);
    }

}
