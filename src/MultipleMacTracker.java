import java.util.*;

final class MultipleMacTracker implements MacTracker {

    final Listener listener;
    // A HashMap is the obvious choice instead of the set, array, count combo below.
    // However, notifyAllTrackers is a profiled hot-spot and the implementation below is
    // considerably faster.
    private final Set<Mac> macs = new HashSet<>();
    private int count = 0;
    private final SingleMacTracker[] trackers = new SingleMacTracker[10_000];

    private MultipleMacTracker(Listener listener) {
        this.listener = listener;
    }
    static MultipleMacTracker of(Listener listener) {
        return new MultipleMacTracker(listener);
    }

    @Override
    public void accept(Packet packet) {
        createAnyMissingTrackers(packet);
        notifyAllTrackers(packet);
    }

    void notifyAllTrackers(Packet packet) {
        for (int i=0; i<count; i++) {
            trackers[i].accept(packet);
        }
    }

    void createAnyMissingTrackers(Packet packet) {
        for (Mac mac : packet.allMacs()) {
            ensureTrackerExistsFor(mac);
        }
    }

    void ensureTrackerExistsFor(Mac mac) {
        if (!macs.contains(mac)) {
            createTrackerFor(mac);
        }
    }

    void createTrackerFor(Mac mac) {
        SingleMacTracker tracker = SingleMacTracker.of(mac,listener);
        macs.add(mac);
        trackers[count] = tracker;
        count++;
    }

}
