import java.util.Objects;

final class IP {

    final Socket source;
    final Socket destination;

    private IP(Socket source, Socket destination) {
        this.source = source;
        this.destination = destination;
    }

    static IP parse(String[] parts) {
        if (!isValid(parts)) {
            return null;
        }
        return new IP(source(parts),destination(parts));
    }

    static Socket source(String[] parts) {
        return Socket.parse(parts[arrow(parts)-1]);
    }

    static Socket destination(String[] parts) {
        return Socket.parse(parts[arrow(parts)+1]);
    }

    static int arrow(String[] parts) {
        for (int i=0; i<parts.length; i++) {
            if (parts[i].equals(">")) {
                return i;
            }
        }
        return -1;
    }

    static boolean isValid(String[] parts) {
        boolean ipFound = false;
        for (String part : parts) {
            if (ipFound && part.equals(">")) {
                return true;
            }
            if (part.startsWith("IP")) {
                ipFound = true;
            }
        }
        return false;
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
        return "IP{" + source + " > " + destination + "}";
    }
}
