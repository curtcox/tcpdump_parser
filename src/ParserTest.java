import org.junit.*;

import java.io.*;

import static org.junit.Assert.*;

public class ParserTest  {

/*
 * There doesn't appear to be any good documentation on the output of tcpdump.
 * You would expect this to be on the man page, but it is not.
 *
 * The best I have found is this:
 * https://github.com/the-tcpdump-group/tcpdump/blob/master/print-802_11.c
 * https://github.com/the-tcpdump-group/tcpdump/blob/master/tcp.h
 * https://github.com/the-tcpdump-group/tcpdump/blob/master/ip.h
 * https://github.com/the-tcpdump-group/tcpdump/blob/master/udp.h
 */

String line1 = "07:21:41.535679 91423200us tsft short preamble 6.0 Mb/s 5200 MHz 11a -70dB signal -99dB noise antenna 1 BSSID:d8:bb:bb:68:ad:bb (oui Unknown) DA:Broadcast SA:5a:5a:5a:5a:ad:be (oui Unknown) Beacon (acmevisitor) [6.0* 9.0 12.0* 18.0 24.0* 36.0 48.0 54.0 Mbit] ESS[|802.11]";
String line2 = "11:37:22.811107 92698955us tsft short preamble 24.0 Mb/s 5200 MHz 11a -74dB signal -99dB noise antenna 1 BSSID:d8:bb:bb:69:ad:bb (oui Unknown) SA:5a:5a:5a:5a:fe:fa (oui Unknown) DA:da:da:da:da:ad:bc (oui Unknown)";
String line3 = "08:58:14.793335 33575772us tsft short preamble 24.0 Mb/s 5240 MHz 11a -65dB signal -99dB noise antenna 1 RA:4a:4a:4a:4a:e4:4d (oui Unknown) Clear-To-Send";
String line4 = "08:58:14.793409 33575885us tsft short preamble 24.0 Mb/s 5240 MHz 11a -65dB signal -99dB noise antenna 1 RA:4a:4a:4a:4a:e4:4d (oui Unknown) BA";
String line5 = "08:58:14.795504 33577940us tsft short preamble 24.0 Mb/s 5240 MHz 11a -76dB signal -99dB noise antenna 1 (H) Unknown Ctrl SubtypeUnknown Ctrl Subtype";
String line6 = "08:58:14.792914 33575259us tsft -65dB signal -99dB noise antenna 1 5240 MHz 11a ht/20 [bit 20] CF +QoS DA:da:da:da:da:e4:4d (oui Unknown) BSSID:bb:bb:bb:bb:d8:7b (oui Unknown) SA:5a:5a:5a:5a:1f:c4 (oui Unknown) LLC, dsap SNAP (0xaa) Individual, ssap SNAP (0xaa) Command, ctrl 0x03: oui Ethernet (0x000000), ethertype IPv4 (0x0800): 17.248.133.169.https > 192.168.14.113.58076: Flags [P.], seq 0:699, ack 1, win 832, options [nop,nop,TS val 828748516 ecr 798386358], length 699";
String line7 = "08:58:14.782486 33564956us tsft short preamble 24.0 Mb/s 5240 MHz 11a -75dB signal -99dB noise antenna 1 RA:4a:4a:4a:4a:d8:7b (oui Unknown) TA:2a:2a:2a:2a:e4:4d (oui Unknown) Request-To-Send";
String line8 = "18:43:57.175984 3776822007us tsft bad-fcs -72dB signal -91dB noise antenna 0 2462 MHz 11g ht/20 72.2 Mb/s MCS 7 20 MHz short GI mixed BCC FEC Strictly Ordered 45us CF +QoS BSSID:ba:ba:ba:ba:3d:a7 SA:5a:5a:5a:5a:76:58 DA:da:da:da:da:ee:fd LLC, dsap Unknown (0xce) Group, ssap Unknown (0x8c) Command, ctrl 0x2000: Information, send seq 0, rcv seq 16, Flags [Command], length 1524";
String line9 = "14:03:38.330729 2554480861us tsft -98dB noise antenna 1 5240 MHz 11a ht/20 [bit 20] CF +QoS DA:da:da:da:da:15:37 BSSID:ba:ba:ba:ba:d8:7b SA:5a:5a:5a:5a:1f:c4 LLC, dsap SNAP (0xaa) Individual, ssap SNAP (0xaa) Command, ctrl 0x03: oui Ethernet (0x000000), ethertype IPv4 (0x0800): 23.44.7.39.80 > 192.168.14.112.61028: Flags [P.], seq 373279892:373280080, ack 3212565907, win 905, options [nop,nop,TS val 2424555772 ecr 49580065], length 188: HTTP: HTTP/1.0 200 OK";
String line10 = "11:02:46.273066 213699562us tsft 1.0 Mb/s 2412 MHz 11g -83dB signal -92dB noise antenna 0 BSSID:ba:ba:ba:ba:22:2e (oui Unknown) DA:da:da:da:da:08:48 (oui Unknown) SA:5a:5a:5a:5a:22:2e (oui Unknown) DeAuthentication: Previous authentication no longer valid";
String line11 = "14:36:37.847579 239222453us tsft bad-fcs 1.0 Mb/s 2412 MHz 11g -76dB signal -83dB noise antenna 0 BSSID:ba:ba:ba:ba:32:1e DA:da:da:da:da:b8:7f SA:5a:5a:5a:5a:5d:f5 Probe Request (Home) [1.0 2.0 5.5 11.0 Mbit][|802.11]";
String line12 = "14:38:09.849876 331222059us tsft 1.0 Mb/s 2412 MHz 11g -73dB signal -83dB noise antenna 0 BSSID:ba:ba:ba:ba:67:1b DA:da:da:da:da:69:18 SA:5a:5a:5a:5a:67:1b Probe Response (MyCharterWiFi1b-2G) [1.0* 2.0* 5.5 11.0 18.0 24.0 36.0 54.0 Mbit] CH: 1, PRIVACY";
String line13 = "13:08:10.022784 2303188775us tsft -54dB signal -95dB noise antenna 0 2412 MHz 11g ht/20 72.2 Mb/s MCS 7 20 MHz short GI mixed BCC FEC [bit 20] CF +QoS DA:00:08:22:a6:1c:fc (oui Unknown) BSSID:8a:15:04:b6:70:80 (oui Unknown) SA:88:15:44:b6:70:80 (oui Unknown) LLC, dsap SNAP (0xaa) Individual, ssap SNAP (0xaa) Command, ctrl 0x03: oui Ethernet (0x000000), ethertype IPv4 (0x0800): 10.128.128.128.domain > 10.121.114.171.btpp2audctr1: 4351 2/0/0 CNAME scontent.xx.fbcdn.net., A 157.240.2.25 (76)";
String line14 = "07:16:21.770393 1180213370us tsft -39dB signal -99dB noise antenna 0 2412 MHz 11g ht/20 72.2 Mb/s MCS 7 20 MHz short GI mixed BCC FEC [bit 20] CF +QoS BSSID:02:18:4a:15:1f:62 (oui Unknown) SA:00:e0:4c:ac:62:23 (oui Unknown) DA:00:18:0a:15:1f:60 (oui Unknown) LLC, dsap SNAP (0xaa) Individual, ssap SNAP (0xaa) Command, ctrl 0x03: oui Ethernet (0x000000), ethertype IPv4 (0x0800): 10.222.173.125.hp-status > 10.128.128.128.domain: 24961+ A? www.googleapis.com. (36)";
String line15 = "21:45:29.292557 IP 10.0.1.6.51185 > 10.0.1.70.rfb: Flags [.], ack 4, win 4191, options [nop,nop,TS val 1924602467 ecr 121440208], length 0";
String line16 = "21:53:13.432996 ba:27:eb:05:20:10 (oui Unknown) > b8:27:eb:05:20:10 (oui Unknown), ethertype Unknown (0x886c), length 86: ";
String line17 = "16:50:50.421556 00:1f:5b:3b:71:14 (oui Unknown) > 00:16:cb:ac:de:e4 (oui Unknown), ethertype IPv6 (0x86dd), length 86: bobs-bass-pro.local.55390 > cooper-mini-3.local.ssh: Flags [.], ack 231736, win 4060, options [nop,nop,TS val 1993124491 ecr 622095219], length 0";

    @Test
    public void parse_returns_a_packet() {
        assert(parse(line1) instanceof Packet);
    }

    Packet parse(String line) {
        return Parser.parse(UnparsedPacket.of(line));
    }

    boolean canParse(String line) {
        return Parser.canParse(UnparsedPacket.of(line));
    }

    @Test
    public void can_parse_valid_lines() {
        assert(canParse(line1));
        assert(canParse(line2));
        assert(canParse(line3));
        assert(canParse(line4));
        assert(canParse(line5));
        assert(canParse(line6));
        assert(canParse(line7));
        assert(canParse(line8));
        assert(canParse(line9));
        assert(canParse(line10));
        assert(canParse(line11));
        assert(canParse(line12));
        assert(canParse(line13));
        assert(canParse(line14));
        assert(canParse(line15));
        assert(canParse(line16));
        assert(canParse(line17));
    }

    @Test
    public void DNS_is_null_when_not_DNS() {
        assertNull(parse(line1).dns);
        assertNull(parse(line2).dns);
    }

    @Test
    public void DNS_query() {
        DNS dns = parse(line14).dns;
        assert(dns.query);
        assertNotNull(dns);
        assertEquals(Host.of("www.googleapis.com"),dns.CNAME);
        assertNull(dns.A);
    }

    @Test
    public void DNS_response() {
        DNS dns = parse(line13).dns;
        assertFalse(dns.query);
        assertNotNull(dns);
        assertEquals(Host.of("scontent.xx.fbcdn.net"),dns.CNAME);
        assertEquals(Host.of("157.240.2.25"),dns.A);
    }

    @Test
    public void type() {
        assertType(line1,"Beacon");
        assertType(line2,null);
        assertType(line3,"Clear-To-Send");
        assertType(line4,"BA");
        assertType(line5,"Unknown");
        assertType(line6,"LLC");
        assertType(line7,"Request-To-Send");
        assertType(line8,"LLC");
        assertType(line9,"LLC");
        assertType(line10,"DeAuthentication");
        assertType(line11,"Probe Request");
        assertType(line12,"Probe Response");
    }

    @Test
    public void line() {
        assertLine(line1);
        assertLine(line2);
        assertLine(line3);
        assertLine(line6);
    }

    void assertLine(String line) {
        assertEquals(parse(line).line,line);
    }

    @Test
    public void localTime() {
        assertEquals(Timestamp.of(07,21,41,535679000),parse(line1).timestamp);
        assertEquals(Timestamp.of(11,37,22,811107000),parse(line2).timestamp);
        assertEquals(Timestamp.of(16,50,50,421556000),parse(line17).timestamp);
    }

    @Test
    public void HTTP() {
        assert(parse(line8).http == null);

        HTTP http = parse(line9).http;
        assertNotNull(http);
        assert(http.length == 188);
        assert(http.status == 200);
    }

    @Test
    public void no_IP() {
        assert(parse(line8).ip == null);
    }

    @Test
    public void IPv4_specified_as_IP() {
        IP ip = parse(line9).ip;
        assertNotNull(ip);
        assertEquals(ip.source.host,Host.of("23.44.7.39"));
        assertEquals(ip.source.port,"80");
        assertEquals(ip.destination.host,Host.of("192.168.14.112"));
        assertEquals(ip.destination.port,"61028");
    }

    @Test
    public void IPv6_specified_as_IPv6() {
        IP ip = parse(line17).ip;
        assertNotNull(ip);
        Socket source = ip.source;
        Socket destination = ip.destination;
        assertNotNull(source);
        assertNotNull(destination);
        assertEquals(source.host,Host.of("bobs-bass-pro.local"));
        assertEquals(source.port,"55390");
        assertEquals(destination.host,Host.of("cooper-mini-3.local"));
        assertEquals(destination.port,"ssh");
    }

    @Test
    public void TCP_when_missing() {
        Packet packet = parse(line13);
        assertNotNull(packet.ip);
        assertNull(packet.ip.tcp);
    }
    
    @Test
    public void flags() {
        assertFlags(parse(line6),"P.");
        assertFlags(parse(line15),".");
    }

    @Test
    public void options() {
        assertOptions(parse(line6),"nop,nop,TS val 828748516 ecr 798386358");
        assertOptions(parse(line9),"nop,nop,TS val 2424555772 ecr 49580065");
        assertOptions(parse(line15),"nop,nop,TS val 1924602467 ecr 121440208");
    }

    @Test
    public void seq() {
        assertSeq(parse(line6),"0:699");
        assertSeq(parse(line9),"373279892:373280080");
    }

    @Test
    public void ack() {
        assertAck(parse(line6),"1");
        assertAck(parse(line9),"3212565907");
        assertAck(parse(line15),"4");
    }

    void assertFlags(Packet packet, String flags) {
        assertEquals(flags,packet.ip.tcp.flags);
    }

    void assertOptions(Packet packet, String options) {
        assertEquals(options,packet.ip.tcp.options);
    }

    void assertSeq(Packet packet, String seq) {
        assertEquals(seq,packet.ip.tcp.seq);
    }

    void assertAck(Packet packet, String ack) {
        assertEquals(ack,packet.ip.tcp.ack);
    }

    @Test
    public void radio_tap_short_preamble() {
        assert(parse(line1).radioTap.shortPreamble);
        assert(parse(line2).radioTap.shortPreamble);
        assert(parse(line6).radioTap.shortPreamble == false);
        assert(parse(line8).radioTap.shortPreamble == false);
    }

    @Test
    public void radio_tap_bad_fcs() {
        assert(parse(line8).radioTap.badFcs);
        assert(parse(line11).radioTap.badFcs);
        assert(parse(line1).radioTap.badFcs == false);
        assert(parse(line2).radioTap.badFcs == false);
    }

    @Test
    public void signal() {
        assertSignal(line1, "-70dB");
        assertSignal(line2, "-74dB");
        assertSignal(line3, "-65dB");
        assertSignal(line4, "-65dB");
        assertSignal(line5, "-76dB");
        assertSignal(line6, "-65dB");
        assertSignal(line7, "-75dB");
        assertSignal(line8, "-72dB");
    }

    @Test
    public void noise() {
        assertNoise(line1, "-99dB");
        assertNoise(line2, "-99dB");
        assertNoise(line3, "-99dB");
        assertNoise(line4, "-99dB");
        assertNoise(line5, "-99dB");
        assertNoise(line6, "-99dB");
        assertNoise(line7, "-99dB");
        assertNoise(line8, "-91dB");
    }

    @Test
    public void offset() {
        assertOffset(line1, 91423200L);
        assertOffset(line2, 92698955L);
        assertOffset(line3, 33575772L);
        assertOffset(line4, 33575885L);
        assertOffset(line5, 33577940L);
        assertOffset(line6, 33575259L);
        assertOffset(line7, 33564956L);
        assertOffset(line8, 3776822007L);
        assertOffset(line15,null);
    }

    @Test
    public void duration() {
        assertDuration(line1, null);
        assertDuration(line2, null);
        assertDuration(line3, null);
        assertDuration(line4, null);
        assertDuration(line5, null);
        assertDuration(line6, null);
        assertDuration(line7, null);
        assertDuration(line8, 45L);
        assertDuration(line9, null);
        assertDuration(line10, null);
    }

    @Test
    public void length() {
        assertLength(line1, null);
        assertLength(line2, null);
        assertLength(line3, null);
        assertLength(line4, null);
        assertLength(line5, null);
        assertLength(line6, 699);
        assertLength(line7, null);
        assertLength(line8, 1524);
        assertLength(line9, 188);
    }

    @Test
    public void BSSID() {
        assertBSSID(line1, "d8:bb:bb:68:ad:bb");
        assertBSSID(line2, "d8:bb:bb:69:ad:bb");
        assertBSSID(line3, null);
        assertBSSID(line4, null);
        assertBSSID(line5, null);
        assertBSSID(line6, "bb:bb:bb:bb:d8:7b");
        assertBSSID(line7, null);
        assertBSSID(line8, "ba:ba:ba:ba:3d:a7");
    }

    @Test
    public void SA() {
        assertSA(line1, "5a:5a:5a:5a:ad:be");
        assertSA(line2, "5a:5a:5a:5a:fe:fa");
        assertSA(line3, null);
        assertSA(line4, null);
        assertSA(line5, null);
        assertSA(line6, "5a:5a:5a:5a:1f:c4");
        assertSA(line7, null);
        assertSA(line8, "5a:5a:5a:5a:76:58");
        assertSA(line11, "5a:5a:5a:5a:5d:f5");
        assertSA(line12, "5a:5a:5a:5a:67:1b");
        assertSA(line16, "ba:27:eb:05:20:10");
        assertSA(line17, "00:1f:5b:3b:71:14");
    }

    @Test
    public void DA() {
        assertDA(line1, "Broadcast");
        assertDA(line2, "da:da:da:da:ad:bc");
        assertDA(line3, null);
        assertDA(line4, null);
        assertDA(line5, null);
        assertDA(line6, "da:da:da:da:e4:4d");
        assertDA(line7, null);
        assertDA(line8, "da:da:da:da:ee:fd");
        assertDA(line11, "da:da:da:da:b8:7f");
        assertDA(line12, "da:da:da:da:69:18");
        assertDA(line16, "b8:27:eb:05:20:10");
        assertDA(line17, "00:16:cb:ac:de:e4");
    }

    @Test
    public void RA() {
        assertRA(line1, null);
        assertRA(line2, null);
        assertRA(line3, "4a:4a:4a:4a:e4:4d");
        assertRA(line4, "4a:4a:4a:4a:e4:4d");
        assertRA(line5, null);
        assertRA(line6, null);
        assertRA(line7, "4a:4a:4a:4a:d8:7b");
        assertRA(line8, null);
        assertRA(line16, null);
        assertRA(line17, null);
    }

    @Test
    public void TA() {
        assertTA(line1, null);
        assertTA(line2, null);
        assertTA(line3, null);
        assertTA(line4, null);
        assertTA(line5, null);
        assertTA(line6, null);
        assertTA(line7, "2a:2a:2a:2a:e4:4d");
        assertTA(line8, null);
        assertTA(line16, null);
        assertTA(line17, null);
    }

    void assertBSSID(String line, String mac) {
        assertMac(parse(line).BSSID, mac(mac));
    }

    void assertSA(String line, String mac) {
        assertMac(parse(line).SA, mac(mac));
    }

    void assertDA(String line, String mac) {
        assertMac(parse(line).DA, mac(mac));
    }

    void assertRA(String line, String mac) {
        assertMac(parse(line).RA, mac(mac));
    }

    void assertTA(String line, String mac) {
        assertMac(parse(line).TA, mac(mac));
    }

    void assertSignal(String line, String signal) {
        assertEquals(parse(line).signal, DB.of(signal));
    }

    void assertType(String line, String type) {
        assertEquals(parse(line).type, Type.of(type));
    }

    void assertNoise(String line, String noise) {
        assertEquals(parse(line).noise, DB.of(noise));
    }

    void assertOffset(String line, Long offset) {
        assertEquals(parse(line).offset, microseconds(offset));
    }

    void assertDuration(String line, Long duration) {
        assertEquals(parse(line).duration, microseconds(duration));
    }

    void assertLength(String line, Integer length) {
        assertEquals(parse(line).length, length);
    }

    Mac mac(String mac) {
        return mac == null ? null : Mac.of(mac);
    }
    Microseconds microseconds(Long microseconds) {
        return microseconds == null ? null : Microseconds.of(microseconds);
    }

    void assertMac(Mac actual, Mac expected) {
        assertEquals(expected,actual);
    }

    @Test
    public void empty_stream_has_no_packets() {
        assertEquals(Parser.parse(() -> stream()).count(),0);
    }

    @Test
    public void stream_with_1_line_has_a_packet() {
        assertEquals(Parser.parse(() -> stream(line1)).count(),1);
    }

    InputStream stream(String... lines) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bytes);
        for (String line : lines) {
            writer.write(line + System.lineSeparator());
        }
        writer.flush();
        return new ByteArrayInputStream(bytes.toByteArray());
    }
}
