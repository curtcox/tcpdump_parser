final class Socket {

    final String host;

    private Socket(String host) {
        this.host = host;
    }

    static Socket parse(String string) {
        if (string.split("\\.").length == 5) {
            return new Socket(host(string));
        } else {
            return null;
        }
    }

    static String host(String string) {
        int lastDot = string.lastIndexOf(".");
        return string.substring(0,lastDot);
    }
}
