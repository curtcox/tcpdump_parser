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
        announce(Main.of(args).namesFromCommandLine());
    }

    static void announce(Map<Mac,String> names) {
        print("tracking " + names);
        Main.forEachReliablePacket(trackerFor(names));
    }

    static MacTracker trackerFor(Map<Mac,String> names) {
        return MacListPresenceTracker.of(names.keySet(),MacPresenceChangeAction.of(MacPresenceChangeAnnouncer.of(names)));
    }

    static void print(String message) {
        System.err.println(message);
    }
}
