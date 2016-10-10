final class Socket {

    final String host;
    final String port;

    private Socket(String host,String port) {
        this.host = host;
        this.port = port;
    }

    static Socket parse(String string) {
        String[] parts = string.split("\\.");
        if (parts.length == 4 || parts.length == 5) {
            return new Socket(host(string),port(string));
        } else {
            return null;
        }
    }

    static String host(String string) {
        int lastDot = string.lastIndexOf(".");
        return string.substring(0,lastDot);
    }

    static String port(String string) {
        int lastDot = string.lastIndexOf(".");
        String port = string.substring(lastDot + 1);
        return port.endsWith(":") ? port.substring(0,port.length() - 1) : port;
    }

    public boolean equals(Object o) {
        return toString().equals(o.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
