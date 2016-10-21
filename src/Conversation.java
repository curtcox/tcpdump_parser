import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

final class Conversation {

    final Socket server;
    final Socket client;
    final List<Packet> packets;
    final LocalTime begin;
    final LocalTime end;
    int length;
    int outgoingPackets = 0;
    int outgoingBytes = 0;
    int incomingPackets = 0;
    int incomingBytes = 0;

    private Conversation(Builder builder) {
        this.packets = builder.packets;
        this.client = builder.client;
        this.server = builder.server;
        this.begin  = builder.begin;
        this.end    = builder.end;
    }

    static class Direction {
        final Socket server;
        final Socket client;
        private Direction(Socket client, Socket server) {
            this.client = client;
            this.server = server;
        }
    }

    private static boolean clientToServer(IP ip) {
        return ip.source.host.privateIP || (!ip.source.host.privateIP && !ip.destination.host.privateIP);
    }


    static Direction directionOf(Packet packet) {
        IP ip = packet.ip;
        if (clientToServer(ip)) {
            return new Direction(ip.source,ip.destination);
        } else {
            return new Direction(ip.destination,ip.source);
        }

    }

    static Builder builder() {
        return new Builder();
    }

    static Conversation of(Packet...packets) {
        Conversation.Builder builder = builder();
        for (Packet packet :packets) {
            builder.add(packet);
        }
        return builder.build();
    }

    static class Builder {
        private Socket server;
        private Socket client;
        private LocalTime begin;
        private LocalTime end;

        List<Packet> packets = new ArrayList<>();

        boolean accepts(Packet packet) {
            return true;
        }

        void add(Packet packet) {
            LocalTime time = packet.localTime;
            if (begin==null) {
                begin = time;
            }
            end   = time;
            packets.add(packet);
            Direction direction = directionOf(packet);
            client = direction.client;
            server = direction.server;
        }

        Conversation build() {
            return new Conversation(this);
        }
    }

    private String line(Packet packet) {
        return String.format("%s %s %s %s %s %s",
                packet.localTime,port(packet),tcp(packet),arrow(packet),length(packet),http(packet));
    }

    private boolean outgoing(Packet packet) {
        return packet.ip.destination.equals(server);
    }

    private String arrow(Packet packet) {
        return outgoing(packet) ? "->" : "<-";
    }

    private static String http(Packet packet) {
        return packet.http == null ? "" : packet.http.toString();
    }

    private static String tcp(Packet packet) {
        TCP tcp = packet.ip.tcp;
        if (tcp == null) {
            return "";
        }
        return tcp.flags + " " + tcp.seq + " " + tcp.ack;
    }

    private String port(Packet packet) {
        return outgoing(packet) ? packet.ip.source.port : packet.ip.destination.port;
    }

    private static String length(Packet packet) {
        return packet.length == null ? "" : packet.length.toString();
    }

}
