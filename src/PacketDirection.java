final class PacketDirection {

    final Socket server;
    final Socket client;
    final boolean outgoing;
    final String arrow;

    private PacketDirection(Socket client, Socket server, boolean outgoing) {
        this.client = client;
        this.server = server;
        this.outgoing = outgoing;
        arrow = outgoing ? "->" : "<-";
    }

    static PacketDirection of(Packet packet) {
        IP ip = packet.ip;
        return ip.clientToServer()
                ? new PacketDirection(ip.source,ip.destination,true)
                : new PacketDirection(ip.destination,ip.source,false);
    }

}
