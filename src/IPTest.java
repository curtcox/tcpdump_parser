import org.junit.Test;

import static org.junit.Assert.*;

public class IPTest {

    String sample1 = "IPv4 (0x0800): 17.248.133.169.https > 192.168.14.113.58076";
    String sample2 = "IPv4 (0x0800): 23.44.7.39.80 > 192.168.14.112.61028";
    String sample3 = "IP 132.245.72.114.https > 10.33.44.33.43114:";
    String sample4 = "IPv4 (0x0800): 10.173.119.71.47276 > sb-in-f188.1e100.net.5228:";
    String sample5 = "IPv6 (0x86dd): fe80::1837:1aaf:ddd3:f7c8.mdns > ff02::fb.mdns:";
    String sample6 = "IPv6 (0x86dd): fe80::9911:2c5a:d92a:83ea > ff02::2:";
    String sample7 = "16:50:50.421556 00:1f:5b:3b:71:14 (oui Unknown) > 00:16:cb:ac:de:e4 (oui Unknown), ethertype IPv6 (0x86dd), length 86: bobs-bass-pro.local.55390 > cooper-mini-3.local.ssh:";

    @Test
    public void toString_contains_source_and_destination_socket() {
        assertToString(sample1,"IP{17.248.133.169:https > 192.168.14.113:58076}");
        assertToString(sample2,"IP{23.44.7.39:80 > 192.168.14.112:61028}");
        assertToString(sample3,"IP{132.245.72.114:https > 10.33.44.33:43114}");
        assertToString(sample4,"IP{10.173.119.71:47276 > sb-in-f188.1e100.net:5228}");
        assertToString(sample5,"IP{fe80::1837:1aaf:ddd3:f7c8:mdns > ff02::fb:mdns}");
        assertToString(sample6,"IP{fe80::9911:2c5a:d92a:83ea > ff02:::2}");
        assertToString(sample7,"IP{bobs-bass-pro.local:55390 > cooper-mini-3.local:ssh}");
    }

    @Test
    public void parse_returns_IP_when_sample_is_valid() {
        assertNotNull(parse(sample1));
        assertNotNull(parse(sample2));
        assertNotNull(parse(sample3));
        assertNotNull(parse(sample4));
        assertNotNull(parse(sample5));
        assertNotNull(parse(sample6));
        assertNotNull(parse(sample7));
    }

    @Test
    public void parse_returns_null_when_sample_is_NOT_valid() {
        assertNull(parse(""));
        assertNull(parse("this is"));
        assertNull(parse("not the key we are looking for"));
        assertNull(parse("IP"));
        assertNull(parse("IPv4"));
    }

    @Test
    public void source() {
        assertSource(sample1,"17.248.133.169.https");
        assertSource(sample2,"23.44.7.39.80");
        assertSource(sample3,"132.245.72.114.https");
        assertSource(sample5,"fe80::1837:1aaf:ddd3:f7c8.mdns");
        assertSource(sample6,"fe80::9911:2c5a:d92a:83ea");
        assertSource(sample7,"bobs-bass-pro.local.55390");
    }

    @Test
    public void destination() {
        assertDestination(sample1,"192.168.14.113.58076");
        assertDestination(sample2,"192.168.14.112.61028");
        assertDestination(sample3,"10.33.44.33.43114");
        assertDestination(sample4,"sb-in-f188.1e100.net.5228");
        assertDestination(sample6,"ff02:::2");
        assertDestination(sample7,"cooper-mini-3.local.ssh");
    }

    @Test
    public void equal() {
        assertEqual(sample1);
        assertEqual(sample2);
        assertEqual(sample3);
        assertEqual(sample4);
        assertEqual(sample5);
        assertEqual(sample6);
        assertEqual(sample7);
    }

    @Test
    public void not_equal() {
        assertNotEqual(sample1,sample2);
        assertNotEqual(sample1,sample3);
        assertNotEqual(sample2,sample3);
        assertNotEqual(sample2,sample6);
    }

    void assertEqual(String sample) {
        IP a = parse(sample);
        IP b = parse(sample);
        assertEquals(a,b);
        assertEquals(b,a);
        assertEquals(a.hashCode(),b.hashCode());
    }

    void assertNotEqual(String samplea, String sampleb) {
        IP a = parse(samplea);
        IP b = parse(sampleb);
        assertNotEquals(a,b);
        assertNotEquals(b,a);
        assertNotEquals(a.hashCode(),b.hashCode());
    }

    void assertToString(String sample, String string) {
        assertEquals(string,parse(sample).toString());
    }

    void assertSource(String sample, String source) {
        assertEquals(Socket.parse(source),parse(sample).source);
    }

    void assertDestination(String sample, String destination) {
        assertEquals(Socket.parse(destination),parse(sample).destination);
    }

    IP parse(String sample) {
        return IP.parse(sample.split(" "));
    }
}
