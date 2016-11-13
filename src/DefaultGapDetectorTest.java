import org.junit.*;

import static org.junit.Assert.assertFalse;

public class DefaultGapDetectorTest {

    DefaultGapDetector detector = DefaultGapDetector.minutes(1);

    @Test
    public void no_gap() {
        assertNoGap(Timestamp.MAX,  Timestamp.MIN);
        assertNoGap(Timestamp.MIN,  Timestamp.MAX);
        assertNoGap(Timestamp.MIN,  Timestamp.MIN);
        assertNoGap(Timestamp.MAX,  Timestamp.MAX);
        assertNoGap(Timestamp.now(),Timestamp.now());
        assertNoGap(Timestamp.of(0,0,0,0),Timestamp.of(0,0,59,0));
    }

    @Test
    public void gap_when_times_are_too_far_apart() {
        assertGap(Timestamp.MIDNIGHT, Timestamp.NOON);
        assertGap(Timestamp.NOON,     Timestamp.MIDNIGHT);
        assertGap(Timestamp.of(0,0,0,0),Timestamp.of(0,1,0,0));
        assertGap(Timestamp.of(0,0,0,0),Timestamp.of(1,0,0,0));
    }

    @Test
    public void gap_when_either_time_is_null() {
        assertGap(null, Timestamp.NOON);
        assertGap(Timestamp.MIDNIGHT,null);
    }

    void assertNoGap(Timestamp t1, Timestamp t2) {
        assertFalse(detector.isGapBetween(t1,t2));
    }

    void assertGap(Timestamp t1, Timestamp t2) {
        assert(detector.isGapBetween(t1,t2));
    }

}
