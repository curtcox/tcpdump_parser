import java.time.LocalTime;

final class DefaultGapDetector implements GapDetector {
    @Override
    public boolean isGapBetween(LocalTime t1, LocalTime t2) {
        return t1 == null || t2 == null;
    }
}
