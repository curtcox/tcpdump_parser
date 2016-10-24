import java.util.Objects;

final class Socket {

    final Host host;
    final String port;

    private Socket(Host host,String port) {
        this.host = host;
        this.port = port;
    }

    static Socket parse(String string) {
        if (string.contains(".") || string.contains(":")) {
            return new Socket(host(string),port(string));
        } else {
            return null;
        }
    }

    static int terminator(String string) {
        int terminator = string.lastIndexOf(".");
        if (terminator < 0) {
            terminator = string.lastIndexOf(":");
            if (terminator == string.length() - 1) {
                terminator = string.substring(0,terminator).lastIndexOf(":") + 1;
            }
        }
        return terminator;
    }

    static Host host(String string) {
        return Host.of(string.substring(0,terminator(string)));
    }

    static String port(String string) {
        int terminator = terminator(string);
        String port = string.substring(terminator);
        port = port.startsWith(":") || port.startsWith(".") ? port.substring(1,port.length()) : port;
        port = port.endsWith(":") ? port.substring(0,port.length() - 1) : port;
        return port;
    }

    public boolean equals(Object o) {
        Socket that = (Socket) o;
        return Objects.equals(port,that.port) && Objects.equals(host,that.host);
    }

    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
