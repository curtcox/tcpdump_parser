import java.time.*;
import java.util.*;
import java.util.stream.*;

final class Conversation {

    final Socket server;
    final Socket client;
    final List<Packet> packets;
    final LocalTime begin;
    final LocalTime end;
    final PacketStats incoming;
    final PacketStats outgoing;
    final List<Message> messages;

    private Conversation(Builder builder) {
        packets  = builder.packets;
        client   = builder.client;
        server   = builder.server;
        begin    = builder.begin;
        end      = builder.end;
        incoming = incomingStats(packets);
        outgoing = outgoingStats(packets);
        messages = Message.messagesFromPackets(packets);
    }

    static class Direction {
        final Socket server;
        final Socket client;
        private Direction(Socket client, Socket server) {
            this.client = client;
            this.server = server;
        }
    }

    static Direction directionOf(Packet packet) {
        IP ip = packet.ip;
        return ip.clientToServer()
            ? new Direction(ip.source,ip.destination)
            : new Direction(ip.destination,ip.source);
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
            return validForThisConversation(packet.ip);
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

        private boolean validForThisConversation(IP ip) {
            if (client == null) {
                return true;
            }
            return validForClientToServer(ip) || validForServerToClient(ip);
        }

        private boolean validForClientToServer(IP ip) {
            return ip.source.equals(client) && ip.destination.equals(server);
        }

        private boolean validForServerToClient(IP ip) {
            return ip.source.equals(server) && ip.destination.equals(client);
        }

    }

    private static PacketStats incomingStats(List<Packet> packets) {
        return PacketStats.fromPackets(packets.stream().filter(p -> !p.ip.clientToServer()));
    }

    private static PacketStats outgoingStats(List<Packet> packets) {
        return PacketStats.fromPackets(packets.stream().filter(p -> p.ip.clientToServer()));
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
