final class DefaultGapDetector implements GapDetector {
    @Override
    public boolean isGapBetween(Timestamp t1, Timestamp t2) {
        return t1 == null || t2 == null;
    }
}
