import java.util.List;
import java.util.stream.Collectors;

final class MacListPresenceTracker implements MacTracker {

    final List<SingleMacPresenceTracker> trackers;

    private MacListPresenceTracker(List<Mac> macs, MacPresenceEvent.Listener listener) {
        trackers = macs.stream().map(m -> SingleMacPresenceTracker.of(m,listener)).collect(Collectors.toList());
    }

    static MacListPresenceTracker of(List<Mac> macs, MacPresenceEvent.Listener listener) {
        return new MacListPresenceTracker(macs,listener);
    }

    @Override
    public void accept(Packet packet) {
        for (SingleMacPresenceTracker tracker : trackers) {
            tracker.accept(packet);
        }
    }
}
