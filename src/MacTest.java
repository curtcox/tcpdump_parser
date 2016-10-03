import org.junit.Test;

import static org.junit.Assert.*;

public class MacTest {

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
}
