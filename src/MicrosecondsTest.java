import org.junit.Test;

import static org.junit.Assert.*;

public class MicrosecondsTest {

    @Test
    public void m0_microseconds() {
        assert(Microseconds.canParse("0us"));
        Microseconds m0 = Microseconds.parse("0us");
        assertEquals(0,   m0.value);
        assertEquals("0", m0.toString());
        assertEquals(m0, Microseconds.of(0L));
        assertEquals(m0.hashCode(), Microseconds.of(0L).hashCode());
    }

    @Test
    public void m10_microseconds() {
        assert(Microseconds.canParse("10us"));
        Microseconds m10 = Microseconds.parse("10us");
        assertEquals(10,   m10.value);
        assertEquals("10", m10.toString());
        assertEquals(m10,  Microseconds.of(10L));
        assertEquals(m10.hashCode(), Microseconds.of(10L).hashCode());
    }

    @Test
    public void m1234567890123456789us_microseconds() {
        assert(Microseconds.canParse("1234567890123456789us"));
        Microseconds big = Microseconds.parse("1234567890123456789us");
        assertEquals(1234567890123456789L,  big.value);
        assertEquals("1234567890123456789", big.toString());
        assertEquals(big,  Microseconds.of(1234567890123456789L));
        assertEquals(big.hashCode(), Microseconds.of(1234567890123456789L).hashCode());
    }

    @Test
    public void parse_returns_null_when_string_is_invalid() {
        assertNull(Microseconds.parse("bogus"));
        assertNull(Microseconds.parse("IP"));
    }

    @Test
    public void different_microseconds_are_not_equal() {
        assertNotEquals(Microseconds.of(0L),Microseconds.of(10L));
    }

    @Test
    public void can_parse() {
        assert(Microseconds.canParse("0us"));
        assert(Microseconds.canParse("1us"));
        assert(Microseconds.canParse("1234us"));
        assert(Microseconds.canParse("123456789us"));
        assert(Microseconds.canParse("1234567890123456789us"));
    }

    @Test
    public void can_not_parse() {
        assertFalse(Microseconds.canParse("Previous"));
        assertFalse(Microseconds.canParse("10.222.173.125.hp-status"));
        assertFalse(Microseconds.canParse("123456789012345678ous"));
    }
}
