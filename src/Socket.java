final class Socket {

    static Socket parse(String string) {
        if (string.split("\\.").length == 5) {
            return new Socket();
        } else {
            return null;
        }
    }
}
