import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class MacTrackerTest {

    Mac mac = Mac.of("01:02:03:04:05:06");
    Listener listener = new Listener();
    MacTracker detector = MacTracker.of(mac,listener);

    static class Listener implements MacTracker.Listener {

        boolean onNewMacPresence;
        boolean onMacDetected;
        MacDetectedEvent event;

        @Override
        public void onNewMacPresence(MacDetectedEvent event) {
            onNewMacPresence = true;
            this.event = event;
        }

        @Override
        public void onMacDetected(MacDetectedEvent event) {
            onMacDetected = true;
            this.event = event;
        }

    }

    @Test
    public void listener_not_triggered_when_no_packets_examined() {
        assertFalse(listener.onNewMacPresence);
        assertFalse(listener.onMacDetected);
    }

    @Test
    public void listener_not_triggered_by_packet_without_MAC() {
        Packet packet = Packet.builder().build();
        detector.accept(packet);
        assertFalse(listener.onMacDetected);
        assertFalse(listener.onNewMacPresence);
    }

    @Test
    public void listener_triggered_by_packet_with_MAC() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        builder.localTime = LocalTime.NOON;
        Packet packet = builder.build();
        detector.accept(packet);
        detector.accept(packet);
        assert(listener.onMacDetected);
        assert(listener.onNewMacPresence);
        assertSame(mac,listener.event.mac);
        assertSame(packet,listener.event.current);
    }

    @Test
    public void new_presence_is_NOT_triggered_when_the_time_gap_from_the_last_presence_is_too_small() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        Packet packet = builder.build();

        MacTracker detector = MacTracker.of(mac,listener,(t1,t2) -> false);
        detector.accept(packet);

        assert(listener.onMacDetected);
        assertFalse(listener.onNewMacPresence);
        assertSame(mac,listener.event.mac);
        assertSame(packet,listener.event.current);
    }

}
