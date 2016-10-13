import java.time.LocalTime;
import java.util.*;

final class Channel {

    final Socket server;
    final Host client;
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
        private Host client;
        private LocalTime begin;
        private LocalTime end;
        private List<Packet> packets = new ArrayList<>();

        Channel build() {
            return new Channel(this);
        }

        boolean accepts(Packet packet) {
            return validForThisChannel(packet.ip);
        }

        void add(Packet packet) {
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
            if (!validForThisChannel(ip)) {
                throw new IllegalArgumentException("Packets in this channel must be between " + client + " and " + server);
            }
        }

        private boolean validForThisChannel(IP ip) {
            if (client == null) {
                return true;
            }
            return clientToServer(ip) || serverToClient(ip);
        }

        private boolean clientToServer(IP ip) {
            return ip.source.host.equals(client) && ip.destination.equals(server);
        }

        private boolean serverToClient(IP ip) {
            return ip.source.equals(server) && ip.destination.host.equals(client);
        }
    }
}
