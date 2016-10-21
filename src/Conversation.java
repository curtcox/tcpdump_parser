import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

final class Conversation {

    Socket server;
    Socket client;
    final List<Packet> packets;
    LocalTime begin;
    LocalTime end;
    int length;
    int outgoingPackets = 0;
    int outgoingBytes = 0;
    int incomingPackets = 0;
    int incomingBytes = 0;

    private Conversation(Builder builder) {
        this.packets = builder.packets;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        List<Packet> packets = new ArrayList<>();

        boolean accepts(Packet packet) {
            return true;
        }

        void add(Packet packet) {
            packets.add(packet);
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
