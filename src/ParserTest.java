import org.junit.Test;
import java.util.*;
import java.time.*;

import static org.junit.Assert.*;

public class ParserTest  {

    String line1 = "07:21:41.535679 91423200us tsft short preamble 6.0 Mb/s 5200 MHz 11a -70dB signal -99dB noise antenna 1 BSSID:d8:bb:bb:68:ad:bb (oui Unknown) DA:Broadcast SA:dd:dd:dd:dd:ad:be (oui Unknown) Beacon (acmevisitor) [6.0* 9.0 12.0* 18.0 24.0* 36.0 48.0 54.0 Mbit] ESS[|802.11]";
    String line2 = "11:37:22.811107 92698955us tsft short preamble 24.0 Mb/s 5200 MHz 11a -74dB signal -99dB noise antenna 1 BSSID:d8:bb:bb:69:ad:bb (oui Unknown) SA:55:55:55:55:fe:fa (oui Unknown) DA:da:da:da:da:ad:bc (oui Unknown)";
    String line3 = "08:58:14.793335 33575772us tsft short preamble 24.0 Mb/s 5240 MHz 11a -65dB signal -99dB noise antenna 1 RA:4a:4a:4a:4a:e4:4d (oui Unknown) Clear-To-Send";
    String line4 = "08:58:14.793409 33575885us tsft short preamble 24.0 Mb/s 5240 MHz 11a -65dB signal -99dB noise antenna 1 RA:4a:4a:4a:4a:e4:4d (oui Unknown) BA";
    String line5 = "08:58:14.795504 33577940us tsft short preamble 24.0 Mb/s 5240 MHz 11a -76dB signal -99dB noise antenna 1 (H) Unknown Ctrl SubtypeUnknown Ctrl Subtype";
    String line6 = "08:58:14.792914 33575259us tsft -65dB signal -99dB noise antenna 1 5240 MHz 11a ht/20 [bit 20] CF +QoS DA:da:da:da:da:e4:4d (oui Unknown) BSSID:bb:bb:bb:bb:d8:7b (oui Unknown) SA:5a:5a:5a:5a:1f:c4 (oui Unknown) LLC, dsap SNAP (0xaa) Individual, ssap SNAP (0xaa) Command, ctrl 0x03: oui Ethernet (0x000000), ethertype IPv4 (0x0800): 17.248.133.169.https > 192.168.14.113.58076: Flags [P.], seq 0:699, ack 1, win 832, options [nop,nop,TS val 828748516 ecr 798386358], length 699";

    @Test
    public void parse_returns_a_packet() {
        assert(parse(line1) instanceof Packet);
    }

    Packet parse(String line) {
        return Parser.parse(line);
    }

    @Test
    public void localTime() {
        assertEquals(LocalTime.of(07,21,41,535679000),parse(line1).localTime);
        assertEquals(LocalTime.of(11,37,22,811107000),parse(line2).localTime);
    }

    @Test
    public void BSSID() {
        assertBSSID(line1, "d8:bb:bb:68:ad:bb");
        assertBSSID(line2, "d8:bb:bb:69:ad:bb");
        assertBSSID(line3, null);
        assertBSSID(line4, null);
        assertBSSID(line5, null);
        assertBSSID(line6, "bb:bb:bb:bb:d8:7b");
    }

    void assertBSSID(String line, String mac) {
        assertMac(parse(line).BSSID,mac == null ? null : Mac.of(mac));
    }

    void assertMac(Mac actual, Mac expected) {
        assertEquals(expected,actual);
    }
}
