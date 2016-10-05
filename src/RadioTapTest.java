import org.junit.Test;

import static org.junit.Assert.*;

public class RadioTapTest {

    @Test
    public void can_create() {
        RadioTap tap = RadioTap.builder().build();
        assertNotNull(tap);
    }

    @Test
    public void bad_fcs() {
        RadioTap.Builder builder = RadioTap.builder();
        builder.badFcs = true;
        assert(builder.build().badFcs);
        builder.badFcs = false;
        assertFalse(builder.build().badFcs);
    }

    @Test
    public void fragmented() {
        RadioTap.Builder builder = RadioTap.builder();
        builder.fragmented = true;
        assert(builder.build().fragmented);
        builder.fragmented = false;
        assertFalse(builder.build().fragmented);
    }

    @Test
    public void wep() {
        RadioTap.Builder builder = RadioTap.builder();
        builder.wep = true;
        assert(builder.build().wep);
        builder.wep = false;
        assertFalse(builder.build().wep);
    }

    @Test
    public void cfp() {
        RadioTap.Builder builder = RadioTap.builder();
        builder.cfp = true;
        assert(builder.build().cfp);
        builder.cfp = false;
        assertFalse(builder.build().cfp);
    }

    @Test
    public void shortPreamble() {
        RadioTap.Builder builder = RadioTap.builder();
        builder.shortPreamble = true;
        assert(builder.build().shortPreamble);
        builder.shortPreamble = false;
        assertFalse(builder.build().shortPreamble);
    }

}