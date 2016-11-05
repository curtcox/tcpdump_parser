final class MultipleMacTracker implements MacTracker {

    final Listener listener;

    private MultipleMacTracker(Listener listener) {
        this.listener = listener;
    }
    static MultipleMacTracker of(Listener listener) {
        return new MultipleMacTracker(listener);
    }

    @Override
    public void accept(Packet packet) {
        SingleMacTracker.of(packet.DA,listener).accept(packet);
    }
}
