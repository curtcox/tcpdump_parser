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

    @Test
    public void new_presence_is_triggered_when_the_time_gap_from_the_last_presence_is_big_enough() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        Packet packet = builder.build();

        MacTracker detector = MacTracker.of(mac,listener,(t1,t2) -> true);
        detector.accept(packet);

        assert(listener.onMacDetected);
        assert(listener.onNewMacPresence);
        assertSame(mac,listener.event.mac);
        assertSame(packet,listener.event.current);
    }

    static class FakeGapDetector implements GapDetector {
        LocalTime t1;
        LocalTime t2;
        @Override
        public boolean isGapBetween(LocalTime t1, LocalTime t2) {
            this.t1 = t1;
            this.t2 = t2;
            return false;
        }
    }

    @Test
    public void gap_detector_is_given_current_and_time_and_null_on_1st_matching_packet() {
        LocalTime t2 = LocalTime.MAX;
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        builder.localTime = t2;
        Packet packet = builder.build();
        FakeGapDetector gapDetector = new FakeGapDetector();

        MacTracker detector = MacTracker.of(mac,listener,gapDetector);

        detector.accept(packet);

        assertNull(gapDetector.t1);
        assertSame(t2,gapDetector.t2);
    }

    @Test
    public void gap_detector_is_given_current_and_time_and_previous_on_2nd_matching_packet() {
        LocalTime t1 = LocalTime.NOON;
        LocalTime t2 = LocalTime.MAX;
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        builder.localTime = t1;
        Packet packet1 = builder.build();
        builder.localTime = t2;
        Packet packet2 = builder.build();
        FakeGapDetector gapDetector = new FakeGapDetector();

        MacTracker detector = MacTracker.of(mac,listener,gapDetector);

        detector.accept(packet1);
        detector.accept(packet2);

        assertSame(t1,gapDetector.t1);
        assertSame(t2,gapDetector.t2);
    }

}
