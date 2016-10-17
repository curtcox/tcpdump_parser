import org.junit.Test;

import static org.junit.Assert.*;

public class SocketTest {

    String sample1 = "17.248.133.169.https";
    String sample2 = "23.44.7.39.80";
    String sample3 = "132.245.72.114.https";
    String sample4 = "192.168.14.113.58076";
    String sample5 = "sb-in-f188.1e100.net.5228:";
    String sample6 = "10.33.44.33.43114:";
    String sample7 = "imap14.mail.vip.bf1.yahoo.com.imaps";
    String sample8 = "fe80::1837:1aaf:ddd3:f7c8.mdns";
    String sample9 = "ff02::fb.mdns:";
    String sample10 = "fe80::9911:2c5a:d92a:83ea";
    String sample11 = "ff02::2:";
    String sample12 = "cooper-mini-3.local.ssh";

    @Test
    public void toString_includes_host_and_port() {
        assertToString(sample1,"17.248.133.169:https");
        assertToString(sample2,"23.44.7.39:80");
        assertToString(sample3,"132.245.72.114:https");
        assertToString(sample4,"192.168.14.113:58076");
        assertToString(sample5,"sb-in-f188.1e100.net:5228");
        assertToString(sample6,"10.33.44.33:43114");
        assertToString(sample7,"imap14.mail.vip.bf1.yahoo.com:imaps");
        assertToString(sample8,"fe80::1837:1aaf:ddd3:f7c8:mdns");
        assertToString(sample9,"ff02::fb:mdns");
        assertToString(sample10,"fe80::9911:2c5a:d92a:83ea");
        assertToString(sample11,"ff02:::2");
        assertToString(sample12,"cooper-mini-3.local:ssh");
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
        assertEqual(sample8);
        assertEqual(sample9);
        assertEqual(sample10);
        assertEqual(sample11);
        assertEqual(sample12);
    }

    @Test
    public void not_equal() {
        String[] samples = new String[]{sample1,sample2,sample3,sample4,sample5,sample6,sample7};
        for (String a : samples) {
            for (String b : samples) {
                if (a!=b) {
                    assertNotEqual(a,b);
                }
            }
        }
    }

    void assertNotEqual(String samplea, String sampleb) {
        Socket a = parse(samplea);
        Socket b = parse(sampleb);
        assertNotEquals(a,b);
        assertNotEquals(a.hashCode(),b.hashCode());
    }

    void assertEqual(String sample) {
        Socket a = parse(sample);
        Socket b = parse(sample);
        assertEquals(a,b);
        assertEquals(a.hashCode(),b.hashCode());
    }

    @Test
    public void parse_returns_socket_when_sample_is_valid() {
        assertNotNull(parse(sample1));
        assertNotNull(parse(sample2));
        assertNotNull(parse(sample3));
        assertNotNull(parse(sample4));
        assertNotNull(parse(sample5));
        assertNotNull(parse(sample6));
        assertNotNull(parse(sample7));
        assertNotNull(parse(sample8));
        assertNotNull(parse(sample9));
        assertNotNull(parse(sample10));
        assertNotNull(parse(sample11));
        assertNotNull(parse(sample12));
    }

    @Test
    public void parse_returns_null_when_sample_is_NOT_valid() {
        assertNull(parse(""));
        assertNull(parse("this is"));
        assertNull(parse("IP"));
        assertNull(parse("IPv4"));
    }

    @Test
    public void host() {
        assertHost(sample1,"17.248.133.169");
        assertHost(sample2,"23.44.7.39");
        assertHost(sample3,"132.245.72.114");
        assertHost(sample4,"192.168.14.113");
        assertHost(sample5,"sb-in-f188.1e100.net");
        assertHost(sample6,"10.33.44.33");
        assertHost(sample7,"imap14.mail.vip.bf1.yahoo.com");
        assertHost(sample8,"fe80::1837:1aaf:ddd3:f7c8");
        assertHost(sample9,"ff02::fb");
        assertHost(sample10,"fe80::9911:2c5a:d92a");
        assertHost(sample11,"ff02::");
        assertHost(sample12,"cooper-mini-3.local");
    }

    @Test
    public void port() {
        assertPort(sample1,"https");
        assertPort(sample2,"80");
        assertPort(sample3,"https");
        assertPort(sample4,"58076");
        assertPort(sample5,"5228");
        assertPort(sample6,"43114");
        assertPort(sample7,"imaps");
        assertPort(sample8,"mdns");
        assertPort(sample9,"mdns");
        assertPort(sample10,"83ea");
        assertPort(sample11,"2");
        assertPort(sample12,"ssh");
    }

    void assertHost(String sample, String host) {
        assertEquals(Host.of(host),parse(sample).host);
    }

    void assertPort(String sample, String port) {
        assertEquals(port, parse(sample).port);
    }

    void assertToString(String sample, String string) {
        assertEquals(string, parse(sample).toString());
    }

    Socket parse(String sample) {
        return Socket.parse(sample);
    }

}
