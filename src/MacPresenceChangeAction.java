import java.util.function.Consumer;

final class MacPresenceChangeAction implements MacPresenceEvent.Listener {

    final Consumer<MacPresenceEvent> consumer;

    private MacPresenceChangeAction(Consumer<MacPresenceEvent> consumer) {
        this.consumer = consumer;
    }

    static MacPresenceChangeAction of(Consumer<MacPresenceEvent> consumer) {
        return new MacPresenceChangeAction(consumer);
    }

    @Override
    public void onNewMacAbsence(MacPresenceEvent event) {
        consumer.accept(event);
    }

    @Override
    public void onNewMacPresence(MacPresenceEvent event) {
        consumer.accept(event);
    }

    @Override
    public void onMacDetected(MacPresenceEvent event) {}
}
