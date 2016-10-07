import org.junit.Test;

import static org.junit.Assert.*;

public class IPTest {

    String sample1 = "IPv4 (0x0800): 17.248.133.169.https > 192.168.14.113.58076";
    String sample2 = "IPv4 (0x0800): 23.44.7.39.80 > 192.168.14.112.61028";
    String sample3 = "IP 132.245.72.114.https > 10.33.44.33.43114:";

    @Test
    public void parse_returns_IP_when_sample_is_valid() {
        assertNotNull(parse(sample1));
        assertNotNull(parse(sample2));
        assertNotNull(parse(sample3));
    }

    @Test
    public void parse_returns_null_when_sample_is_NOT_valid() {
        assertNull(parse(""));
        assertNull(parse("this is"));
        assertNull(parse("not the key we are looking for"));
        assertNull(parse("IP"));
        assertNull(parse("IPv4"));
    }

    IP parse(String sample) {
        return IP.parse(sample.split(" "));
    }
}
