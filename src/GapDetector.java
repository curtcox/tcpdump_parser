import java.time.LocalTime;

interface GapDetector {
    boolean isGapBetween(LocalTime t1, LocalTime t2);
}
