import java.util.Objects;

final class IP {

    final Socket source;
    final Socket destination;
    final TCP tcp;

    private IP(Socket source, Socket destination, TCP tcp) {
        this.source = source;
        this.destination = destination;
        this.tcp = tcp;
    }

    static IP parse(Fields parts) {
        if (!isValid(parts)) {
            return null;
        }
        return new IP(source(parts),destination(parts),TCP.parse(parts));
    }

    static Socket source(Fields parts) {
        return Socket.parse(parts.at(arrow(parts)-1));
    }

    static Socket destination(Fields parts) {
        return Socket.parse(parts.at(arrow(parts)+1));
    }

    static int arrow(Fields parts) {
        boolean ipFound = false;
        for (int i=0; i<parts.length(); i++) {
            String part = parts.at(i);
            if (ipFound && part.equals(">")) {
                return i;
            }
            if (part.startsWith("IP")) {
                ipFound = true;
            }
        }
        return -1;
    }

    boolean clientToServer() {
        return source.host.privateIP || (!source.host.privateIP && !destination.host.privateIP);
    }

    static boolean isValid(Fields parts) {
        return arrow(parts) > -1;
    }

    @Override
    public boolean equals(Object o) {
        IP that = (IP) o;
        return Objects.equals(source,that.source) && Objects.equals(destination,that.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source,destination);
    }

    @Override
    public String toString() {
        if (tcp==null) {
            return "IP{" + source + " > " + destination + "}";
        } else {
            return "IP{" + source + " > " + destination + " " + tcp +"}";
        }
    }
}
