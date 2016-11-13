final class SingleMacSignalTracker implements MacTracker {

    private final Mac mac;
    private final MacSignalStrengthEvent.Listener listener;
    private Packet lastSeenPacketWithMAC;

    private SingleMacSignalTracker(Mac mac, MacSignalStrengthEvent.Listener listener) {
        this.mac = mac;
        this.listener = listener;
    }

    static SingleMacSignalTracker of(Mac mac, MacSignalStrengthEvent.Listener listener) {
        return new SingleMacSignalTracker(mac,listener);
    }

    @Override
    public void accept(Packet packet) {
        if (!packet.contains(mac)) {
            return;
        }
        if (changeInSignalStrength(packet)) {
            listener.accept(eventFrom(packet));
        }
        lastSeenPacketWithMAC = packet;
    }

    private boolean changeInSignalStrength(Packet packet) {
        return  lastSeenPacketWithMAC == null ||
                packet.signal != lastSeenPacketWithMAC.signal ||
                packet.noise  != lastSeenPacketWithMAC.noise;
    }

    private MacSignalStrengthEvent eventFrom(Packet packet) {
        return MacSignalStrengthEvent.of(mac,packet,lastSeenPacketWithMAC);
    }

    public static void main(String[] args) {
        Mac mac = args.length < 1 ? Mac.all0 : Mac.of(args[0]);
        print("Listening for " + mac);
        Main.forEachReliablePacket(SingleMacSignalTracker.of(mac,e -> {print(e);}));
    }

    static void print(Object o) {
        System.err.println(o);
    }

}
