import java.time.*;
import java.time.temporal.*;

final class Timestamp {

    final LocalTime localTime;
    static final Timestamp MIN = new Timestamp(LocalTime.MIN);
    static final Timestamp MAX = new Timestamp(LocalTime.MAX);
    static final Timestamp NOON = new Timestamp(LocalTime.NOON);
    static final Timestamp MIDNIGHT = new Timestamp(LocalTime.MIDNIGHT);

    private Timestamp(LocalTime localTime) {
        this.localTime = localTime;
    }

    static Timestamp now() {
        return new Timestamp(LocalTime.now());
    }

    static Timestamp parse(Fields fields) {
        String[] parts = fields.at(0).split(":");
        int hour   = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = Integer.parseInt(parts[2].split("\\.")[0]);
        int nano   = Integer.parseInt(parts[2].split("\\.")[1]) * 1000;
        return of(hour,minute,second,nano);
    }

    static Timestamp of(int hour, int minute, int second, int nanoOfSecond) {
        return new Timestamp(LocalTime.of(hour,minute,second,nanoOfSecond));
    }

    boolean isBefore(Timestamp other) {
        return localTime.isBefore(other.localTime);
    }

    public Timestamp truncatedToSeconds() {
        return new Timestamp(localTime.truncatedTo(ChronoUnit.SECONDS));
    }

    @Override
    public boolean equals(Object o) {
        Timestamp that = (Timestamp) o;
        return localTime.equals(that.localTime);
    }

    @Override
    public int hashCode() {
        return localTime.hashCode();
    }

    @Override
    public String toString() {
        return localTime.toString();
    }
}
