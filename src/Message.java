import java.time.*;
import java.util.*;

final class Message {

    final Socket server;
    final Socket client;
    final boolean request;
    final List<Packet> packets;
    final LocalTime begin;
    final LocalTime end;
    final PacketStats stats;

    private Message(Builder builder) {
        this.client = builder.client;
        this.server = builder.server;
        this.request = builder.request;
        this.packets = builder.packets;
        this.begin = builder.begin;
        this.end = builder.end;
        stats = PacketStats.fromPackets(packets.stream());
    }

    static List<Message> messagesFromPackets(List<Packet> packets) {
        List<Message> messages = new ArrayList<>();
        Builder builder = new Builder();
        for (Packet packet : packets) {
            if (builder.isThisPacketPartOfMessageBeingBuilt(packet)) {
                builder.add(packet);
            } else {
                messages.add(builder.build());
                builder = new Builder();
                builder.add(packet);
            }
        }
        if (!builder.packets.isEmpty()) {
            messages.add(builder.build());
        }

        return messages;
    }

    static class Builder {
        Socket server;
        Socket client;
        boolean request = false;
        LocalTime begin = null;
        LocalTime end;
        List<Packet> packets = new ArrayList<>();

        void add(Packet packet) {
            packets.add(packet);
            if (begin == null) {
                begin = packet.localTime;
            }
            end = packet.localTime;
            Conversation.Direction direction = Conversation.directionOf(packet);
            server = direction.server;
            client = direction.client;
        }

        boolean isThisPacketPartOfMessageBeingBuilt(Packet packet) {
            return packets.isEmpty() || packets.get(0).ip.source.equals(packet.ip.source);
        }

        Message build() {
            return new Message(this);
        }
    }

    String summary() {
        String packetSummary = String.format("-> %s / %s <- %s / %s",stats.packets,stats.bytes,stats.packets,stats.bytes);
        return String.format("%s - %s packets: %s", begin ,end, packetSummary);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(summary() + System.lineSeparator());
        return out.toString();
    }
}
