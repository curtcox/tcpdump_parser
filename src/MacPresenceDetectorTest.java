import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class MacPresenceDetectorTest {

    Mac mac = Mac.of("01:02:03:04:05:06");
    Listener listener = new Listener();
    MacPresenceDetector detector = new MacPresenceDetector(mac,listener);

    static class Listener implements MacPresenceDetector.Listener {

        boolean triggered;
        Mac mac;
        Packet packet;

        @Override
        public void onMacDetected(Mac mac, Packet packet) {
            triggered = true;
            this.mac = mac;
            this.packet = packet;
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
