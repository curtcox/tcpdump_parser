import java.util.*;

final class MultipleMacTracker implements MacTracker {

    final Listener listener;
    final Map<Mac,SingleMacTracker> trackers = new HashMap<>();

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
        for (MacTracker tracker : trackers.values()) {
            tracker.accept(packet);
        }
    }

    void createAnyMissingTrackers(Packet packet) {
        for (Mac mac : packet.allMacs()) {
            ensureTrackerExistsFor(mac);
        }
    }

    void ensureTrackerExistsFor(Mac mac) {
        if (!trackers.containsKey(mac)) {
            trackers.put(mac,SingleMacTracker.of(mac,listener));
        }
    }
}
