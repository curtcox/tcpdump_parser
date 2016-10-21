import org.junit.Test;

import static org.junit.Assert.*;

public class DNSTest {

    String sample1 = "4351 2/0/0 CNAME scontent.xx.fbcdn.net., A 157.240.2.25 (76)";
    String sample2 = "24961+ A? www.googleapis.com. (36)";

    @Test
    public void can_parse_returns_value_when_valid() {
        assertNotNull(parse(sample1));
        assertNotNull(parse(sample2));
    }

    @Test
    public void can_parse_returns_null_when_not_valid() {
        assertNull(parse(""));
        assertNull(parse("Not empty or valid"));
    }

    @Test
    public void query() {
        assertFalse(parse(sample1).query);
        assert(parse(sample2).query);
    }

    @Test
    public void CNAME() {
        assertEquals(Host.of("scontent.xx.fbcdn.net"),parse(sample1).CNAME);
        assertEquals(Host.of("www.googleapis.com"),parse(sample2).CNAME);
    }

    @Test
    public void A() {
        assertEquals(Host.of("157.240.2.25"),parse(sample1).A);
        assertNull(parse(sample2).A);
    }

    DNS parse(String sample) {
        return DNS.parse(sample.split(" "));
    }
}
