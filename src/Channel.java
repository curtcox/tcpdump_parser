import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Channel implements Comparable<Channel> {

    final Socket server;
    final Host client;
    final List<Conversation> conversations;
    final Timestamp begin;
    final Timestamp end;
    final PacketStats incoming;
    final PacketStats outgoing;

    private Channel(Builder builder) {
        server  = builder.server;
        client  = builder.client;
        begin   = builder.begin;
        end     = builder.end;
        conversations = Collections.unmodifiableList(builder.conversations.stream().map(b->b.build()).collect(Collectors.toList()));
        incoming = incomingStats(conversations);
        outgoing = outgoingStats(conversations);
    }

    static Channel of(Packet...packets) {
        Builder builder = builder();
        for (Packet packet : packets) {
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
        private Timestamp begin;
        private Timestamp end;
        private List<Conversation.Builder> conversations = new ArrayList<>();

        Channel build() {
            return new Channel(this);
        }

        boolean accepts(Packet packet) {
            return validForThisChannel(packet.ip);
        }

        void add(Packet packet) {
            Timestamp time = packet.timestamp;
            check(time);
            if (begin==null) {
                begin = time;
            }
            end   = time;
            IP ip = packet.ip;
            check(ip);
            if (client==null) {
                PacketDirection direction = PacketDirection.of(packet);
                client = direction.client.host;
                server = direction.server;
            }
            addPacketToConversations(packet);
        }

        private void addPacketToConversations(Packet packet) {
            for (Conversation.Builder conversation : conversations) {
                if (conversation.accepts(packet)) {
                    conversation.add(packet);
                    return;
                }
            }
            Conversation.Builder conversation = Conversation.builder();
            conversation.add(packet);
            conversations.add(conversation);
        }

        private void check(Timestamp time) {
            if (time==null) {
                throw new IllegalArgumentException("Time missing, but required for timeline conversations.");
            }
            if (begin !=null && time.isBefore(begin)) {
                throw new IllegalArgumentException("Packets must be added in chronological order.");
            }
        }

        private void check(IP ip) {
            if (ip==null) {
                throw new IllegalArgumentException("IP missing, but required for timeline conversations.");
            }
            if (!validForThisChannel(ip)) {
                throw new IllegalArgumentException("Packets in this channel must be between " + client + " and " + server);
            }
        }

        private boolean validForThisChannel(IP ip) {
            if (client == null) {
                return true;
            }
            return validForClientToServer(ip) || validForServerToClient(ip);
        }

        private boolean validForClientToServer(IP ip) {
            return ip.source.host.equals(client) && ip.destination.equals(server);
        }

        private boolean validForServerToClient(IP ip) {
            return ip.source.equals(server) && ip.destination.host.equals(client);
        }
    }

    private static PacketStats incomingStats(List<Conversation> conversations) {
        return PacketStats.fromPackets(packets(conversations).filter(p -> !p.ip.clientToServer()));
    }

    private static PacketStats outgoingStats(List<Conversation> conversations) {
        return PacketStats.fromPackets(packets(conversations).filter(p -> p.ip.clientToServer()));
    }

    private static Stream<Packet> packets(List<Conversation> conversations) {
        return conversations.stream().flatMap(c -> c.packets.stream());
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(summary() + System.lineSeparator());
        return out.toString();
    }

    String transcript() {
        StringBuilder out = new StringBuilder();
        for (Conversation conversation : conversations) {
            out.append(conversation.toString() + System.lineSeparator());
            out.append(conversation.transcript() + System.lineSeparator());
        }
        return out.toString();
    }

    String summary() {
        String conversationSummary = String.format("%s / %s -><- %s / %s",outgoing.packets,outgoing.bytes,incoming.packets,incoming.bytes);
        return String.format("client: %s server: %s begin: %s end: %s %s conversations: %s", client, server, begin ,end, conversations.size(),conversationSummary);
    }

    @Override
    public int compareTo(Channel that) {
        return client.compareTo(that.client);
    }


}
