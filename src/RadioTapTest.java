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

    @Test
    public void parse_recognizes_fragmented_key() {
        RadioTap tap = parse("fragmented");
        assert(tap.fragmented);
        assertFalse(tap.shortPreamble);
        assertFalse(tap.badFcs);
        assertFalse(tap.cfp);
        assertFalse(tap.wep);
    }

    @Test
    public void parse_recognizes_short_preamble_key() {
        RadioTap tap = parse("short preamble");
        assert(tap.shortPreamble);
        assertFalse(tap.fragmented);
        assertFalse(tap.badFcs);
        assertFalse(tap.cfp);
        assertFalse(tap.wep);
    }

    @Test
    public void parse_recognizes_bad_fcs_key() {
        RadioTap tap = parse("bad-fcs");
        assert(tap.badFcs);
        assertFalse(tap.fragmented);
        assertFalse(tap.shortPreamble);
        assertFalse(tap.cfp);
        assertFalse(tap.wep);
    }

    @Test
    public void parse_recognizes_cfp_key() {
        RadioTap tap = parse("cfp");
        assert(tap.cfp);
        assertFalse(tap.fragmented);
        assertFalse(tap.shortPreamble);
        assertFalse(tap.badFcs);
        assertFalse(tap.wep);
    }

    @Test
    public void parse_recognizes_wep_key() {
        RadioTap tap = parse("wep");
        assert(tap.wep);
        assertFalse(tap.fragmented);
        assertFalse(tap.shortPreamble);
        assertFalse(tap.badFcs);
        assertFalse(tap.cfp);
    }

    @Test
    public void parse_sets_flags_false_if_missing() {
        RadioTap tap = parse("junk");
        assertFalse(tap.fragmented);
        assertFalse(tap.shortPreamble);
        assertFalse(tap.badFcs);
        assertFalse(tap.cfp);
        assertFalse(tap.wep);
    }

    @Test
    public void parse_sets_flags_true_if_all_present() {
        RadioTap tap = parse("fragmented short preamble bad-fcs cfp wep");
        assert(tap.fragmented);
        assert(tap.shortPreamble);
        assert(tap.badFcs);
        assert(tap.cfp);
        assert(tap.wep);
    }

    RadioTap parse(String fields) {
        return RadioTap.parse(Fields.of(fields));
    }
}