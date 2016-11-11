final class SingleMacSignalTracker implements MacTracker {

    private final Mac mac;
    private final MacPresenceEvent.Listener listener;
    private Packet lastSeenPacketWithMAC;

    private SingleMacSignalTracker(Mac mac, MacPresenceEvent.Listener listener) {
        this.mac = mac;
        this.listener = listener;
    }

    static SingleMacSignalTracker of(Mac mac, MacPresenceEvent.Listener listener) {
        return new SingleMacSignalTracker(mac,listener);
    }

    @Override
    public void accept(Packet packet) {
    }

    public static void main(String[] args) {
        Mac mac = args.length < 1 ? Mac.all0 : Mac.of(args[0]);
        MacPresenceEvent.Listener listener = new MacPresenceChangeAction(e->{System.out.println(e);});
        Parser.parse(() -> System.in).reliable()
                .forEach(packet -> SingleMacSignalTracker.of(mac,listener));
    }
}
