import org.junit.Test;

import static org.junit.Assert.*;

public class DNSTest {

    String sample1 = "4351 2/0/0 CNAME site.whatever.net., A 157.240.2.25 (76)";
    String sample2 = "24961+ A? www.googleapis.com. (36)";

    @Test
    public void toString_contains_expected_value() {
        assertEquals(parse(sample1).toString(),"DNS:{CNAME=site.whatever.net A=157.240.2.25}");
        assertEquals(parse(sample2).toString(),"DNS:{query CNAME=www.googleapis.com}");
    }

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
        assertEquals(Host.of("site.whatever.net"),parse(sample1).CNAME);
        assertEquals(Host.of("www.googleapis.com"),parse(sample2).CNAME);
    }

    @Test
    public void A() {
        assertEquals(Host.of("157.240.2.25"),parse(sample1).A);
        assertNull(parse(sample2).A);
    }

    DNS parse(String sample) {
        return DNS.parse(Fields.of(sample));
    }
}
