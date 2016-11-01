import java.util.*;

final class Message {

    final Socket server;
    final Socket client;
    final boolean request;
    final List<Packet> packets;
    final Timestamp begin;
    final Timestamp end;
    final PacketStats stats;
    final PacketDirection direction;

    private Message(Builder builder) {
        this.client    = builder.client;
        this.server    = builder.server;
        this.request   = builder.request;
        this.packets   = builder.packets;
        this.begin     = builder.begin;
        this.end       = builder.end;
        this.direction = builder.direction;
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
        Timestamp begin = null;
        Timestamp end;
        List<Packet> packets = new ArrayList<>();
        PacketDirection direction;

        void add(Packet packet) {
            packets.add(packet);
            if (begin == null) {
                begin = packet.localTime;
            }
            end = packet.localTime;
            if (direction==null) {
                direction = PacketDirection.of(packet);
                server = direction.server;
                client = direction.client;
            }
        }

        boolean isThisPacketPartOfMessageBeingBuilt(Packet packet) {
            return packets.isEmpty() || packets.get(0).ip.source.equals(packet.ip.source);
        }

        Message build() {
            return new Message(this);
        }
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
        return PacketDirection.of(packet).outgoing ? packet.ip.source.port : packet.ip.destination.port;
    }

    private String line(Packet packet) {
        String arrow = PacketDirection.of(packet).arrow;
        return String.format("%s %s %s %s %s %s",
                packet.localTime,port(packet),tcp(packet),arrow,length(packet),http(packet));
    }

    private static String length(Packet packet) {
        return packet.length == null ? "" : packet.length.toString();
    }

    String transcript() {
        StringBuilder out = new StringBuilder();
        for (Packet packet : packets) {
            out.append(line(packet) + System.lineSeparator());
        }
        return out.toString();
    }

    String http() {
        for (Packet packet : packets) {
            if (packet.http != null) {
                HTTP http = packet.http;
                if (http.request) {
                    return String.format("%s %s",http.verb,http.url);
                } else {
                    if (http.status != null) {
                        return Integer.toString(http.status);
                    }
                }
            }
        }
        return "";
    }

    String summary() {
        String packetSummary = String.format("%s %s",direction.arrow,stats.bytes);
        return String.format("%s - %s %s %s packets: %s", begin ,end, http(), packets.size(), packetSummary);
    }

    @Override
    public String toString() {
        return summary();
    }
}
