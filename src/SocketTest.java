import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SocketTest {

    String sample1 = "17.248.133.169.https";
    String sample2 = "23.44.7.39.80";
    String sample3 = "132.245.72.114.https";
    String sample4 = "192.168.14.113.58076";
    String sample5 = "192.168.14.112.61028";
    String sample6 = "10.33.44.33.43114:";

    @Test
    public void parse_returns_socket_when_sample_is_valid() {
        assertNotNull(parse(sample1));
        assertNotNull(parse(sample2));
        assertNotNull(parse(sample3));
        assertNotNull(parse(sample4));
        assertNotNull(parse(sample5));
        assertNotNull(parse(sample6));
    }

    @Test
    public void parse_returns_null_when_sample_is_NOT_valid() {
        assertNull(parse(""));
        assertNull(parse("this is"));
        assertNull(parse("not.the.key.we.are.looking:for"));
        assertNull(parse("IP"));
        assertNull(parse("IPv4"));
    }

    Socket parse(String sample) {
        return Socket.parse(sample);
    }

}
