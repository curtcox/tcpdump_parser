import org.junit.Test;

import static org.junit.Assert.*;

public class MacTest {

    @Test
    public void of_returns_null_when_giben_an_invalid_MAC_string() {
        assertNull(Mac.of("bogus mac"));
        assertNull(Mac.of("(oui"));
        assertNull(Mac.of("Unknown)"));
    }

    @Test
    public void Macs_with_same_string_are_equal() {
        assertEquals(Mac.of("11:22:33:44:55:66"),Mac.of("11:22:33:44:55:66"));
    }

    @Test
    public void Macs_with_different_strings_are_not_equal() {
        assertNotEquals(Mac.of("00:22:33:44:55:66"),Mac.of("11:22:33:44:55:66"));
    }

    @Test
    public void toString_matches_value() {
        Mac mac = Mac.of("88:66:77:55:33:00");
        assertSame(mac.value,mac.toString());
    }

    @Test
    public void vendor_returns_first_3_bytes() {
        assertEquals(Mac.of("01:23:45:55:33:00").vendor,"01:23:45");
        assertEquals(Mac.of("88:66:77:55:33:00").vendor,"88:66:77");
    }

    @Test
    public void serial_returns_last_3_bytes() {
        assertEquals(Mac.of("01:23:45:65:43:21").serial,"65:43:21");
        assertEquals(Mac.of("88:66:77:55:33:00").serial,"55:33:00");
    }

    @Test
    public void equals_returns_false_for_null() {
        assertFalse(Mac.of("00:00:00:00:00:00").equals(null));
    }
}
