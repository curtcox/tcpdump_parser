final class MultipleMacTracker implements MacTracker {

    final Listener listener;
    MacTracker tracker;

    private MultipleMacTracker(Listener listener) {
        this.listener = listener;
    }
    static MultipleMacTracker of(Listener listener) {
        return new MultipleMacTracker(listener);
    }

    @Override
    public void accept(Packet packet) {
        if (tracker==null) {
            tracker = SingleMacTracker.of(packet.DA,listener);
        }
        tracker.accept(packet);
    }
}
