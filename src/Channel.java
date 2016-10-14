import java.time.LocalTime;
import java.util.*;

final class Channel implements Comparable<Channel> {

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
            if (client==null) {
                if (clientToServer(ip)) {
                    client = ip.source.host;
                    server = ip.destination;
                } else {
                    client = ip.destination.host;
                    server = ip.source;
                }
            }

            packets.add(packet);
        }

        private static boolean clientToServer(IP ip) {
            return ip.source.host.privateIP || (!ip.source.host.privateIP && !ip.destination.host.privateIP);
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
        out.append(summaryLine() + System.lineSeparator());
        for (Packet packet : packets) {
            out.append(line(packet) + System.lineSeparator());
        }
        return out.toString();
    }

    private String summaryLine() {
        int outgoingPackets = 0;
        int outgoingBytes = 0;
        int incomingPackets = 0;
        int incomingBytes = 0;
        for (Packet packet : packets) {
            if (outgoing(packet)) {
                outgoingPackets++;
                outgoingBytes += packet.length;
            } else {
                incomingPackets++;
                incomingBytes += packet.length;
            }
        }
        String packets = String.format("-> %s / %s <- %s / %s",outgoingPackets,outgoingBytes,incomingPackets,incomingBytes);
        return String.format("client: %s server: %s begin: %s end: %s packets: %s", client, server, begin ,end, packets);
    }

    private String line(Packet packet) {
        return String.format("%s %s %s %s %s",packet.localTime,port(packet),arrow(packet),length(packet),http(packet));
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

    private String port(Packet packet) {
        return outgoing(packet) ? packet.ip.source.port : packet.ip.destination.port;
    }

    private static String length(Packet packet) {
        return packet.length == null ? "" : packet.length.toString();
    }

    @Override
    public int compareTo(Channel that) {
        return client.compareTo(that.client);
    }


}
