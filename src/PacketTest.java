import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class PacketTest {

    @Test
    public void toString_does_not_supply_unspecified_fields() {
        Packet.Builder builder = Packet.builder();
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{}");
    }

    @Test
    public void toString_contains_localTime_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = LocalTime.NOON;
        assertStringIs(builder.build(),"Packet:{localTime=12:00}");
        builder.localTime = LocalTime.MIDNIGHT;
        assertStringIs(builder.build(),"Packet:{localTime=00:00}");
        builder.localTime = LocalTime.MAX;
        assertStringIs(builder.build(),"Packet:{localTime=23:59:59.999999999}");
    }

    @Test
    public void toString_contains_BSSID_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.BSSID = mac("ba");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{BSSID=ba}");
    }

    @Test
    public void toString_contains_SA_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.SA = mac("5a");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{SA=5a}");
    }

    @Test
    public void toString_contains_DA_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac("da");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{DA=da}");
    }

    @Test
    public void toString_contains_TA_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.TA = mac("2a");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{TA=2a}");
    }

    @Test
    public void toString_contains_RA_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.RA = mac("4a");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{RA=4a}");
    }

    @Test
    public void toString_contains_length_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.length = 42;
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{length=42}");
    }

    @Test
    public void toString_contains_duration_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.duration = Microseconds.of(42L);
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{duration=42}");
    }

    @Test
    public void toString_contains_signal_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.signal = "-9dB";
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{signal=-9dB}");
    }

    @Test
    public void toString_contains_noise_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.noise = "-2dB";
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{noise=-2dB}");
    }

    @Test
    public void toString_contains_offset_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.offset = Microseconds.of(42L);
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{offset=42}");
    }

    @Test
    public void toString_contains_all_Macs_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.BSSID = mac("b1");
        builder.SA = mac("52");
        builder.RA = mac("43");
        builder.DA = mac("d4");
        builder.TA = mac("25");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{BSSID=b1, DA=d4, RA=43, SA=52, TA=25}");
    }

    void assertStringIs(Packet packet, String string) {
        assertEquals(string,packet.toString());
    }

    Mac mac(String mac) {
        return Mac.of(mac);
    }
}
