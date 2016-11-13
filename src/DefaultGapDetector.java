import static java.lang.Math.*;

final class DefaultGapDetector implements GapDetector {

    final long gap;
    static final long minute        = 1_000_000_000L * 60;
    static final long full_cycle = 1_000_000_000L * 60 * 60 * 24;

    private DefaultGapDetector(long gap) {
        this.gap = gap;
    }

    static DefaultGapDetector minutes(int minutes) {
        return new DefaultGapDetector(minutes * minute);
    }

    @Override
    public boolean isGapBetween(Timestamp t1, Timestamp t2) {
        return t1 == null ||
               t2 == null ||
               tooFarApart(t1,t2);
    }

    boolean tooFarApart(Timestamp t1, Timestamp t2) {
        long n1 = t1.toTotalNanos();
        long n2 = t2.toTotalNanos();
        long first = min(n1,n2);
        long last  = max(n1,n2);
        return diff(first,last)              >= gap &&
               diff(first + full_cycle,last) >= gap;
    }

    long diff(long n1, long n2) {
        return abs(n1 - n2);
    }

}
