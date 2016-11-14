import java.util.*;
import java.util.stream.Collectors;

final class MacListPresenceTracker implements MacTracker {

    final MacTracker trackers;

    private MacListPresenceTracker(Collection<Mac> macs, MacPresenceEvent.Listener listener) {
        trackers = MacTrackerList.of(
                macs.stream().map(m -> PassiveMacPresenceTracker.of(m,listener)).collect(Collectors.toList()));
    }

    static MacListPresenceTracker of(Collection<Mac> macs, MacPresenceEvent.Listener listener) {
        return new MacListPresenceTracker(macs,listener);
    }

    @Override
    public void accept(Packet packet) {
        trackers.accept(packet);
    }
}
