import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class MultipleMacTrackerTest {

    Mac mac = Mac.of("01:02:03:04:05:06");
    Mac mac1 = Mac.of("00:02:03:04:05:07");
    Mac mac2 = Mac.of("00:02:03:04:05:08");
    Mac mac3 = Mac.of("00:02:03:04:05:09");
    Mac mac4 = Mac.of("00:02:03:04:05:0a");
    Mac mac5 = Mac.of("00:02:03:04:05:0b");
    Listener listener = new Listener();
    MultipleMacTracker detector = MultipleMacTracker.of(listener);

    static class Listener implements MacTracker.Listener {
        Map<Mac,MacDetectedEvent> presence = new HashMap<>();
        Map<Mac,MacDetectedEvent> detected = new HashMap<>();
        Map<Mac,MacDetectedEvent> absence = new HashMap<>();

        @Override
        public void onNewMacAbsence(MacDetectedEvent event) {
            absence.put(event.mac,event);
        }

        @Override
        public void onNewMacPresence(MacDetectedEvent event) {
            presence.put(event.mac,event);
        }

        @Override
        public void onMacDetected(MacDetectedEvent event) {
            detected.put(event.mac,event);
        }

        void reset() {
            presence.clear();
            detected.clear();
            absence.clear();
        }

        void assertAbsenceEvent(Mac... macs) {
            for (Mac mac : macs) {
                MacDetectedEvent event = absence.get(mac);
                assertNotNull(event);
                assertSame(mac,    event.mac);
            }
        }

        void assertDetectedEvent(Packet packet, Mac... macs) {
            for (Mac mac : macs) {
                MacDetectedEvent event = detected.get(mac);
                assertNotNull(event);
                assertSame(mac,    event.mac);
                assertSame(packet, event.current);
            }
        }

        void assertPresenceEvent(Packet packet, Mac... macs) {
            for (Mac mac : macs) {
                MacDetectedEvent event = presence.get(mac);
                assertNotNull(event);
                assertSame(mac,    event.mac);
                assertSame(packet, event.current);
            }
        }

        void assertNoEvents() {
            assert(detected.isEmpty());
            assert(presence.isEmpty());
            assert(absence.isEmpty());
        }

    }

    @Test
    public void listener_not_triggered_when_no_packets_examined() {
        listener.assertNoEvents();
    }


    @Test
    public void listener_triggered_by_packet_with_MAC() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac;
        builder.localTime = Timestamp.now();
        Packet packet = builder.build();
        detector.accept(packet);

        listener.assertPresenceEvent(packet,mac);
        listener.assertDetectedEvent(packet,mac);
    }

    @Test
    public void listener_triggered_for_every_MAC_in_packet() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac1;
        builder.SA = mac2;
        builder.RA = mac3;
        builder.TA = mac4;
        builder.BSSID = mac5;
        builder.localTime = Timestamp.now();
        Packet packet = builder.build();
        detector.accept(packet);
        listener.assertPresenceEvent(packet,mac1,mac2,mac3,mac4,mac5);
        listener.assertDetectedEvent(packet,mac1,mac2,mac3,mac4,mac5);
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

        listener.assertAbsenceEvent(mac);
    }

}
