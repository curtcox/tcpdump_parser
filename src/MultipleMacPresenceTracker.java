import java.util.*;

final class MultipleMacPresenceTracker implements MacTracker {

    final Listener listener;
    private final Map<Mac,SingleMacTracker> macs = new HashMap<>();
    private SingleMacTracker[] trackers = new SingleMacTracker[0];

    private MultipleMacPresenceTracker(Listener listener) {
        this.listener = listener;
    }
    static MultipleMacPresenceTracker of(Listener listener) {
        ListenerWrapper wrapper = new ListenerWrapper(listener);
        MultipleMacPresenceTracker trackers = new MultipleMacPresenceTracker(wrapper);
        wrapper.trackers = trackers;
        return trackers;
    }

    static class ListenerWrapper implements Listener {
        final Listener listener;
        MultipleMacPresenceTracker trackers;

        ListenerWrapper(Listener listener) {
            this.listener = listener;
        }

        @Override
        public void onNewMacAbsence(MacPresenceEvent event) {
            listener.onNewMacAbsence(event);
            trackers.removeTrackerFor(event.mac);
        }

        @Override
        public void onNewMacPresence(MacPresenceEvent event) {
            listener.onNewMacPresence(event);
        }

        @Override
        public void onMacDetected(MacPresenceEvent event) {
            listener.onMacDetected(event);
        }
    }

    @Override
    public void accept(Packet packet) {
        createAnyMissingTrackers(packet);
        notifyAllTrackers(packet);
    }

    void notifyAllTrackers(Packet packet) {
        for (SingleMacTracker tracker : trackers) {
            tracker.accept(packet);
        }
    }

    void createAnyMissingTrackers(Packet packet) {
        for (Mac mac : packet.allMacs()) {
            ensureTrackerExistsFor(mac);
        }
    }

    void ensureTrackerExistsFor(Mac mac) {
        if (!macs.containsKey(mac)) {
            createTrackerFor(mac);
        }
    }

    void createTrackerFor(Mac mac) {
        SingleMacTracker tracker = SingleMacTracker.of(mac,listener);
        macs.put(mac,tracker);
        createTrackerArray();
    }

    void removeTrackerFor(Mac mac) {
        macs.remove(mac);
        createTrackerArray();
    }

    void createTrackerArray() {
        trackers = macs.values().toArray(new SingleMacTracker[macs.size()]);
    }
}
