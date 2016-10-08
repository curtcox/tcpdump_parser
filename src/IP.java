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
        for (int i=0; i<parts.length; i++) {
            if (parts[i].equals(">")) {
                return Socket.parse(parts[i-1]);
            }
        }
        return null;
    }

    static Socket destination(String[] parts) {
        for (int i=0; i<parts.length; i++) {
            if (parts[i].equals(">")) {
                return Socket.parse(parts[i+1]);
            }
        }
        return null;
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
}
