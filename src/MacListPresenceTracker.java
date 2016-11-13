import java.util.List;
import java.util.stream.Collectors;

final class MacListPresenceTracker implements MacTracker {

    final MacTracker trackers;

    private MacListPresenceTracker(List<Mac> macs, MacPresenceEvent.Listener listener) {
        trackers = MacTrackerList.of(
                macs.stream().map(m -> SingleMacPresenceTracker.of(m,listener)).collect(Collectors.toList()));
    }

    static MacListPresenceTracker of(List<Mac> macs, MacPresenceEvent.Listener listener) {
        return new MacListPresenceTracker(macs,listener);
    }

    @Override
    public void accept(Packet packet) {
        trackers.accept(packet);
    }
}
