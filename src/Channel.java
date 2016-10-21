import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

final class Channel implements Comparable<Channel> {

    final Socket server;
    final Host client;
    final List<Conversation> conversations;
    final LocalTime begin;
    final LocalTime end;

    private Channel(Builder builder) {
        server  = builder.server;
        client  = builder.client;
        begin   = builder.begin;
        end     = builder.end;
        conversations = Collections.unmodifiableList(builder.conversations.stream().map(b->b.build()).collect(Collectors.toList()));
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
            Conversation.Builder builder = Conversation.builder();
            builder.add(packet);
            conversations.add(builder);
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
            return validForclientToServer(ip) || validForserverToClient(ip);
        }

        private boolean validForclientToServer(IP ip) {
            return ip.source.host.equals(client) && ip.destination.equals(server);
        }

        private boolean validForserverToClient(IP ip) {
            return ip.source.equals(server) && ip.destination.host.equals(client);
        }
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
        int outgoingPackets = 0;
        int outgoingBytes = 0;
        int incomingPackets = 0;
        int incomingBytes = 0;
        for (Conversation conversation : conversations) {
            outgoingPackets += conversation.outgoingPackets;
            incomingPackets += conversation.incomingPackets;
            incomingBytes   += conversation.outgoingBytes;
            outgoingBytes   += conversation.incomingBytes;
        }
        String packets = String.format("-> %s / %s <- %s / %s",outgoingPackets,outgoingBytes,incomingPackets,incomingBytes);
        return String.format("client: %s server: %s begin: %s end: %s conversations: %s", client, server, begin ,end, packets);
    }


    @Override
    public int compareTo(Channel that) {
        return client.compareTo(that.client);
    }


}
