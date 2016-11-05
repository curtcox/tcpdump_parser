import org.junit.*;

import static org.junit.Assert.*;

public class MultipleMacTrackerTest {

    Mac mac = Mac.of("01:02:03:04:05:06");
    Listener listener = new Listener();
    MultipleMacTracker detector = MultipleMacTracker.of(listener);

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

    void assertNoEvent() {
        assertFalse(listener.onMacDetected);
        assertFalse(listener.onNewMacPresence);
        assertFalse(listener.onNewMacAbsence);
    }

    @Test
    public void listener_triggered_by_packet_with_MAC() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        builder.localTime = Timestamp.now();
        Packet packet = builder.build();
        detector.accept(packet);

        assertPresenceEvent();
        assertCurrentEvent(packet);
    }

    @Test
    public void absence_triggered_after_gap() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        builder.localTime = Timestamp.NOON;
        Packet packet1 = builder.build();
        builder.localTime = Timestamp.MIDNIGHT;
        builder.DA = null;
        Packet packet2 = builder.build();
        detector.accept(packet1);
        listener.reset();
        detector.accept(packet2);

        assertAbsenceEvent();
    }

    void assertAbsenceEvent() {
        assertFalse(listener.onMacDetected);
        assertFalse(listener.onNewMacPresence);
        assert(listener.onNewMacAbsence);
    }

    void assertCurrentEvent(Packet packet) {
        MacDetectedEvent event = listener.event;
        assertSame(mac,    event.mac);
        assertSame(packet, event.current);
    }

    void assertPresenceEvent() {
        assert(listener.onMacDetected);
        assert(listener.onNewMacPresence);
        assertFalse(listener.onNewMacAbsence);
    }
}
