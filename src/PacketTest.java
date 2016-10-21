import org.junit.Test;

import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;

public class PacketTest {

    @Test
    public void toString_does_not_supply_unspecified_fields() {
        Packet.Builder builder = Packet.builder();
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{}");
    }

    @Test
    public void toString_contains_DNS_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.dns = DNS.parse("24961+ A? google.com. (36)".split(" "));
        assertStringIs(builder.build(),"Packet:{dns=DNS:{query CNAME=google.com}}");
    }

    @Test
    public void toString_contains_IP_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.ip = IP.parse("IP 132.245.72.114.https > 10.33.44.33.43114:".split(" "));
        assertStringIs(builder.build(),"Packet:{ip=IP{132.245.72.114:https > 10.33.44.33:43114}}");
    }

    @Test
    public void toString_contains_TCP_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.ip = IP.parse("IP 132.245.72.114.https > 10.33.44.33.43114: Flags [P.],".split(" "));
        assertStringIs(builder.build(),"Packet:{ip=IP{132.245.72.114:https > 10.33.44.33:43114 TCP:{flags=P.}}}");
    }

    @Test
    public void toString_contains_HTTP_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.http = HTTP.parse("length 188: HTTP: HTTP/1.0 200 OK".split(" "));
        assertStringIs(builder.build(),"Packet:{http=HTTP:{length=188, status=200}}");
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
        builder.BSSID = mac("ba:ba:ba:ba:ba:ba");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{BSSID=ba:ba:ba:ba:ba:ba}");
    }

    @Test
    public void toString_contains_SA_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.SA = mac("5a:5a:5a:5a:5a:5a");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{SA=5a:5a:5a:5a:5a:5a}");
    }

    @Test
    public void toString_contains_DA_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.DA = mac("da:da:da:da:da:da");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{DA=da:da:da:da:da:da}");
    }

    @Test
    public void toString_contains_TA_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.TA = mac("2a:2a:2a:2a:2a:2a");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{TA=2a:2a:2a:2a:2a:2a}");
    }

    @Test
    public void toString_contains_RA_when_specified() {
        Packet.Builder builder = Packet.builder();
        builder.RA = mac("4a:4a:4a:4a:4a:4a");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{RA=4a:4a:4a:4a:4a:4a}");
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
        builder.BSSID = mac("b1:b1:b1:b1:b1:b1");
        builder.SA = mac("52:52:52:52:52:52");
        builder.RA = mac("43:43:43:43:43:43");
        builder.DA = mac("d4:d4:d4:d4:d4:d4");
        builder.TA = mac("25:25:25:25:25:25");
        Packet packet = builder.build();
        assertStringIs(packet,"Packet:{BSSID=b1:b1:b1:b1:b1:b1, DA=d4:d4:d4:d4:d4:d4, RA=43:43:43:43:43:43, SA=52:52:52:52:52:52, TA=25:25:25:25:25:25}");
    }

    void assertStringIs(Packet packet, String string) {
        assertEquals(string,packet.toString());
    }

    Mac mac(String mac) {
        return Mac.of(mac);
    }

    IP ip(String ip) {
        return IP.parse(ip.split(" "));
    }

    @Test
    public void allMacs_exactly_contains_all_Macs_when_all_specified() {
        Packet.Builder builder = Packet.builder();
        Mac BSSID = mac("b1:b1:b1:b1:b1:b1");
        Mac SA = mac("52:52:52:52:52:52");
        Mac RA = mac("43:43:43:43:43:43");
        Mac DA = mac("d4:d4:d4:d4:d4:d4");
        Mac TA = mac("25:25:25:25:25:25");
        builder.BSSID = BSSID;
        builder.SA = SA;
        builder.RA = RA;
        builder.DA = DA;
        builder.TA = TA;
        Packet packet = builder.build();
        Set<Mac> all = new HashSet<>(Arrays.asList(new Mac[]{BSSID,SA,RA,DA,TA}));
        assertEquals(packet.allMacs(),all);
    }

    @Test
    public void contains_is_true_for_all_contained_macs() {
        Packet.Builder builder = Packet.builder();
        Mac BSSID = mac("b1:b1:b1:b1:b1:b1");
        Mac SA = mac("52:52:52:52:52:52");
        Mac RA = mac("43:43:43:43:43:43");
        Mac DA = mac("d4:d4:d4:d4:d4:d4");
        Mac TA = mac("25:25:25:25:25:25");
        builder.BSSID = BSSID;
        builder.SA = SA;
        builder.RA = RA;
        builder.DA = DA;
        builder.TA = TA;
        Packet packet = builder.build();
        assert(packet.contains(BSSID));
        assert(packet.contains(SA));
        assert(packet.contains(RA));
        assert(packet.contains(DA));
        assert(packet.contains(TA));
    }

    @Test
    public void contains_is_false_for_all_not_contained_macs() {
        Packet.Builder builder = Packet.builder();
        Mac BSSID = mac("b1:b1:b1:b1");
        Mac SA = mac("52:52:52:52");
        Mac RA = mac("43:43:43:43");
        Mac DA = mac("d4:d4:d4:d4");
        Mac TA = mac("25:25:25:25");
        builder.BSSID = BSSID;
        builder.SA = SA;
        builder.RA = RA;
        builder.DA = DA;
        builder.TA = TA;
        Packet packet = builder.build();
        assertFalse(packet.contains(mac("01:02:03:04:05:06")));
    }

    @Test
    public void contains_is_false_for_null_macs_when_Macs_are_null() {
        Packet.Builder builder = Packet.builder();
        Packet packet = builder.build();
        assertTrue(packet.contains((Mac)null));
    }

    @Test
    public void allMacs_exactly_contains_all_Macs_when_none_specified() {
        Packet.Builder builder = Packet.builder();
        Packet packet = builder.build();
        assert(packet.allMacs() instanceof Set);
        assert(packet.allMacs().isEmpty());
    }

    @Test
    public void packets_are_equal_when_all_they_have_is_MACs_but_the_MACs_are_the_same() {
        Packet.Builder builder = Packet.builder();
        assertEqual(builder.build(),builder.build());
        builder.BSSID = mac("b1:b1:b1:b1");
        assertEqual(builder.build(),builder.build());
        builder.SA = mac("52:52:52:52");
        assertEqual(builder.build(),builder.build());
        builder.RA = mac("43:43:43:43");
        assertEqual(builder.build(),builder.build());
        builder.DA = mac("d4:d4:d4:d4");
        assertEqual(builder.build(),builder.build());
        builder.TA = mac("25:25:25:25");
        assertEqual(builder.build(),builder.build());
    }

    @Test
    public void packets_are_equal_when_all_they_have_is_IPs_but_the_IPs_are_the_same() {
        Packet.Builder builder = Packet.builder();
        assertEqual(builder.build(),builder.build());
        builder.ip = ip("IP 132.245.72.114.https > 10.33.44.33.43114");
        assertEqual(builder.build(),builder.build());
    }

    @Test
    public void packets_are_equal_when_all_they_have_is_times_but_the_time_are_the_same() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = LocalTime.NOON;
        assertEqual(builder.build(),builder.build());
    }

    @Test
    public void packets_are_not_equal_when_they_have_different_times() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = LocalTime.NOON;
        Packet noon = builder.build();
        builder.localTime = LocalTime.MIDNIGHT;
        Packet midnight = builder.build();
        assertNotEqual(noon,midnight);
    }

    @Test
    public void packets_are_not_equal_when_all_they_have_is_IPs_but_the_IPs_are_different() {
        Packet.Builder builder = Packet.builder();
        assertEqual(builder.build(),builder.build());
        builder.ip = ip("IP 132.245.72.114.https > 10.33.44.33.43114");
        Packet a = builder.build();
        builder.ip = ip("IP 192.168.0.1.https > 10.33.44.33.43114");
        Packet b = builder.build();
        assertNotEqual(a,b);
    }

    @Test
    public void packets_are_not_equal_when_BSSID_is_different() {
        Packet.Builder builder = Packet.builder();
        Packet without = builder.build();
        builder.BSSID  = mac("b1:b1:b1:b1:b1:b1");
        Packet with    = builder.build();
        assertNotEqual(with,without);
    }

    @Test
    public void packets_are_not_equal_when_SA_is_different() {
        Packet.Builder builder = Packet.builder();
        Packet without = builder.build();
        builder.SA  = mac("5a:5a:5a:5a:5a:5a");
        Packet with    = builder.build();
        assertNotEqual(with,without);
    }

    @Test
    public void packets_are_not_equal_when_RA_is_different() {
        Packet.Builder builder = Packet.builder();
        Packet without = builder.build();
        builder.RA  = mac("b1:b1:b1:b1:b1:b1");
        Packet with    = builder.build();
        assertNotEqual(with,without);
    }

    @Test
    public void packets_are_not_equal_when_TA_is_different() {
        Packet.Builder builder = Packet.builder();
        Packet without = builder.build();
        builder.TA  = mac("2a:2a:2a:2a:2a:2a");
        Packet with    = builder.build();
        assertNotEqual(with,without);
    }

    @Test
    public void packets_are_not_equal_when_DA_is_different() {
        Packet.Builder builder = Packet.builder();
        Packet without = builder.build();
        builder.DA  = mac("da:da:da:da:da:da");
        Packet with    = builder.build();
        assertNotEqual(with,without);
    }

    void assertEqual(Packet a, Packet b) {
        assertEquals(a,b);
        assertEquals(b,a);
        assertEquals(a.hashCode(),b.hashCode());
    }

    void assertNotEqual(Packet a, Packet b) {
        assertNotEquals(a,b);
        assertNotEquals(b,a);
        assertNotEquals(a.hashCode(),b.hashCode());
    }

}
