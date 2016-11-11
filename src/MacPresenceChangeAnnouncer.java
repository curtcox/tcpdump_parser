import java.util.*;
import java.util.function.Consumer;

final class MacPresenceChangeAnnouncer implements Consumer<MacPresenceEvent> {

    final Map<Mac,String> names;
    private MacPresenceChangeAnnouncer(Map<Mac,String> names) {
        this.names = names;
    }

    static MacPresenceChangeAnnouncer of(Map<Mac,String> names) {
        return new MacPresenceChangeAnnouncer(names);
    }

    @Override
    public void accept(MacPresenceEvent event) {
        String name = names.get(event.mac);
        say(String.format("%s %s",name,event.present ? "arrived" : "left"));
    }

    void say(String message) {
        Speaker.say(message);
    }

    public static void main(String[] args) {
        announce(namesFromCommandLine(args));
    }

    static void announce(Map<Mac,String> names) {
        print("tracking " + names);
        MacTracker tracker = trackerFor(names);
        Parser.parse(() -> System.in).reliable()
                .forEach(packet -> tracker.accept(packet));
    }

    static MacTracker trackerFor(Map<Mac,String> names) {
        List<Mac> macs = new ArrayList(names.keySet());
        MacTracker.Listener listener = new MacPresenceChangeAction(MacPresenceChangeAnnouncer.of(names));
        return MacListTracker.of(macs,listener);
    }

    static Map<Mac,String> namesFromCommandLine(String[] args) {
        Map<Mac,String> names = new HashMap<>();
        for (String arg : args) {
            String[] parts = arg.split("=");
            names.put(Mac.of(parts[0]),parts[1]);
        }
        return names;
    }

    static void print(String message) {
        System.err.println(message);
    }
}
