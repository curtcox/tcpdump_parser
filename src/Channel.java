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
            LocalTime time = packet.localTime;
            check(time);
            if (begin==null) {
                begin = time;
            }
            end    = time;
            IP ip = packet.ip;
            check(ip);
            client = ip.source.host;
            server = ip.destination;
            packets.add(packet);
        }

        private void check(LocalTime time) {
            if (time==null) {
                throw new IllegalArgumentException("Time missing, but required for timeline packets.");
            }
            if (begin !=null && time.isBefore(begin)) {
                throw new IllegalArgumentException("Packets must be added in chronological order.");
            }
        }

        private void check(IP ip) {
            if (ip==null) {
                throw new IllegalArgumentException("IP missing, but required for timeline packets.");
            }
        }
    }
}
