import java.util.*;

final class MacTrackerList implements MacTracker {
    final List<MacTracker> trackers;

    private MacTrackerList(List<MacTracker> trackers) {
        this.trackers = trackers;
    }

    static MacTracker of(List<MacTracker> trackers) {
        return new MacTrackerList(trackers);
    }

    static MacTracker of(MacTracker... trackers) {
        return new MacTrackerList(Arrays.asList(trackers));
    }

    @Override
    public void accept(Packet packet) {
        for (MacTracker tracker : trackers) {
            tracker.accept(packet);
        }
    }

}
