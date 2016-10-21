import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

final class Channel implements Comparable<Channel> {

    final Socket server;
    final Host client;
    final List<Conversation> conversations;
    final LocalTime begin;
    final LocalTime end;
    final int outgoingPackets;
    final int outgoingBytes;
    final int incomingPackets = 0;
    final int incomingBytes = 0;

    private Channel(Builder builder) {
        server  = builder.server;
        client  = builder.client;
        begin   = builder.begin;
        end     = builder.end;
        conversations = Collections.unmodifiableList(builder.conversations.stream().map(b->b.build()).collect(Collectors.toList()));
        outgoingPackets = outgoingPackets(conversations);
        outgoingBytes   = outgoingBytes(conversations);
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
        private List<Conversation.Builder> conversations = new ArrayList<>();

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
            end   = time;
            IP ip = packet.ip;
            check(ip);
            if (client==null) {
                Conversation.Direction direction = Conversation.directionOf(packet);
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

        private void check(LocalTime time) {
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


    private int outgoingBytes(List<Conversation> conversations) {
        for (Conversation conversation : conversations) {
            for (Packet packet : conversation.packets) {
                return packet.length == null ? 0 : packet.length;
            }
        }
        return 0;
    }

    private int outgoingPackets(List<Conversation> conversations) {
        return conversations.size();
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
        }
        return out.toString();
    }

    String summary() {
        String packets = String.format("-> %s / %s <- %s / %s",outgoingPackets,outgoingBytes,incomingPackets,incomingBytes);
        return String.format("client: %s server: %s begin: %s end: %s conversations: %s", client, server, begin ,end, packets);
    }


    @Override
    public int compareTo(Channel that) {
        return client.compareTo(that.client);
    }


}
