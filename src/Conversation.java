import java.time.*;
import java.util.*;

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
            PacketDirection direction = PacketDirection.of(packet);
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

   String summary() {
        String messageSummary = String.format("%s / %s -><- %s / %s",outgoing.packets,outgoing.bytes,incoming.packets,incoming.bytes);
        return String.format("%s %s - %s %s messages: %s", client.port, begin ,end, messages.size(), messageSummary);
    }

    String transcript() {
        StringBuilder out = new StringBuilder();
        for (Message message : messages) {
            out.append(message.toString() + System.lineSeparator());
        }
        return out.toString();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(summary() + System.lineSeparator());
        return out.toString();
    }
}
