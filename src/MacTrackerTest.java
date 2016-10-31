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
        boolean onNewMacAbsence;
        MacDetectedEvent event;

        @Override
        public void onNewMacAbsence(MacDetectedEvent event) {
            onNewMacAbsence = true;
            this.event = event;
        }

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

        void reset() {
            onMacDetected = false;
            onNewMacPresence = false;
            onNewMacAbsence = false;
            event = null;
        }
    }

    @Test
    public void listener_not_triggered_when_no_packets_examined() {
        assertNoEvent();
    }

    @Test
    public void listener_not_triggered_by_packet_without_MAC() {
        Packet packet = Packet.builder().build();
        detector.accept(packet);
        assertNoEvent();
    }

    void assertNoEvent() {
        assertFalse(listener.onMacDetected);
        assertFalse(listener.onNewMacPresence);
        assertFalse(listener.onNewMacAbsence);
    }

    void assertOnlyDetectedEvent() {
        assert(listener.onMacDetected);
        assertFalse(listener.onNewMacPresence);
        assertFalse(listener.onNewMacAbsence);
    }

    void assertPresenceEvent() {
        assert(listener.onMacDetected);
        assert(listener.onNewMacPresence);
        assertFalse(listener.onNewMacAbsence);
    }

    void assertAbsenceEvent() {
        assertFalse(listener.onMacDetected);
        assertFalse(listener.onNewMacPresence);
        assert(listener.onNewMacAbsence);
    }

    @Test
    public void listener_triggered_by_packet_with_MAC() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        builder.localTime = LocalTime.NOON;
        Packet packet = builder.build();
        detector.accept(packet);
        detector.accept(packet);

        assertPresenceEvent();
        assertCurrentEvent(packet);
    }

    void assertCurrentEvent(Packet packet) {
        MacDetectedEvent event = listener.event;
        assertSame(mac,    event.mac);
        assertSame(packet, event.current);
    }

    void assertPreviousEvent(Packet packet) {
        MacDetectedEvent event = listener.event;
        assertSame(mac,    event.mac);
        assertSame(packet, event.previous);
    }

    @Test
    public void new_presence_is_NOT_triggered_when_the_time_gap_from_the_last_presence_is_too_small() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        Packet packet = builder.build();

        MacTracker detector = MacTracker.of(mac,listener,(t1,t2) -> false);
        detector.accept(packet);

        assertOnlyDetectedEvent();
        assertCurrentEvent(packet);
    }

    @Test
    public void new_presence_is_triggered_when_the_time_gap_from_the_last_presence_is_big_enough() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        Packet packet = builder.build();

        MacTracker detector = MacTracker.of(mac,listener,(t1,t2) -> true);
        detector.accept(packet);

        assertPresenceEvent();
        assertCurrentEvent(packet);
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
    public void on_1st_matching_packet_gap_detector_is_given_current_and_time_and_null() {
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
    public void on_1st_matching_after_other_non_matching_packet_gap_detector_is_given_current_and_time_and_null() {
        LocalTime t2 = LocalTime.MAX;
        Packet.Builder builder = Packet.builder();
        builder.localTime = t2;
        Packet packet1 = builder.build();
        builder.DA = mac;
        Packet packet2 = builder.build();
        FakeGapDetector gapDetector = new FakeGapDetector();

        MacTracker detector = MacTracker.of(mac,listener,gapDetector);

        detector.accept(packet1);
        detector.accept(packet2);

        assertNull(gapDetector.t1);
        assertSame(t2,gapDetector.t2);
    }

    @Test
    public void on_2nd_matching_packet_gap_detector_is_given_current_and_time_and_previous() {
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

    @Test
    public void on_1st_NON_matching_after_matching_packet_gap_detector_is_given_current_and_time_and_previous() {
        LocalTime t1 = LocalTime.MIN;
        LocalTime t2 = LocalTime.MAX;
        Packet.Builder builder = Packet.builder();
        builder.localTime = t1;
        builder.DA = mac;
        Packet packet1 = builder.build();
        builder.localTime = t2;
        builder.DA = null;
        Packet packet2 = builder.build();
        FakeGapDetector gapDetector = new FakeGapDetector();

        MacTracker detector = MacTracker.of(mac,listener,gapDetector);

        detector.accept(packet1);
        detector.accept(packet2);

        assertSame(t1,gapDetector.t1);
        assertSame(t2,gapDetector.t2);
    }

    @Test
    public void on_1st_NON_matching_after_matching_packet_triggers_absence() {
        LocalTime t1 = LocalTime.MIN;
        LocalTime t2 = LocalTime.MAX;
        Packet.Builder builder = Packet.builder();
        builder.localTime = t1;
        builder.DA = mac;
        Packet packet1 = builder.build();
        builder.localTime = t2;
        builder.DA = null;
        Packet packet2 = builder.build();

        MacTracker detector = MacTracker.of(mac,listener,(ta,tb) -> true);

        detector.accept(packet1);
        listener.reset();
        detector.accept(packet2);

        assertAbsenceEvent();
        assertCurrentEvent(null);
        assertPreviousEvent(packet1);
    }

}
