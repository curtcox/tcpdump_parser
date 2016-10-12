import java.time.LocalTime;
import java.util.*;

final class Channel {

    final Socket server;
    final String client;
    final List<Packet> packets;
    final LocalTime begin;
    final LocalTime end;


    private Channel(Builder builder) {
        server  = builder.server;
        client  = builder.client;
        begin   = builder.begin;
        end     = builder.end;
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
        private LocalTime begin;
        private LocalTime end;
        private List<Packet> packets = new ArrayList<>();

        Channel build() {
            return new Channel(this);
        }

        public void add(Packet packet) {
            begin  = packet.localTime;
            end    = packet.localTime;
            IP ip = packet.ip;
            if (ip==null) {
                throw new IllegalArgumentException("IP missing, but required for timeline packets.");
            }
            client = ip.source.host;
            server = ip.destination;
            packets.add(packet);
        }
    }
}
