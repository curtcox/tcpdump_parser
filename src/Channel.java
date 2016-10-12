import java.util.*;

final class Channel {

    final Socket server;
    final String client;
    final List<Packet> packets;

    private Channel(Builder builder) {
        server = builder.server;
        client = builder.client;
        packets = Collections.unmodifiableList(builder.packets);
    }

    static Channel of(Packet...packets) {
        Builder builder = builder();
        for (Packet packet :packets) {
            builder.add(packet);
        }
        return builder.build();
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private Socket server;
        private String client;
        private List<Packet> packets = new ArrayList<>();

        Channel build() {
            return new Channel(this);
        }

        public void add(Packet packet) {
            client = packet.ip.source.host;
            server = packet.ip.destination;
            packets.add(packet);
        }
    }
}
