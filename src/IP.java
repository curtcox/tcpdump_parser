import java.util.Objects;

final class IP {

    final Socket source;
    final Socket destination;
    final String seq;
    final String ack;
    final String flags;
    final String options;

    private IP(Socket source, Socket destination,String seq,String ack,String flags,String options) {
        this.source = source;
        this.destination = destination;
        this.seq = seq;
        this.ack = ack;
        this.flags = flags;
        this.options = options;
    }

    static IP parse(String[] parts) {
        if (!isValid(parts)) {
            return null;
        }
        return new IP(source(parts),destination(parts),seq(parts),ack(parts),flags(parts),options(parts));
    }

    static Socket source(String[] parts) {
        return Socket.parse(parts[arrow(parts)-1]);
    }

    static Socket destination(String[] parts) {
        return Socket.parse(parts[arrow(parts)+1]);
    }

    static String seq(String[] parts) {
        for (int i=0; i<parts.length; i++) {
            String part = parts[i];
            if (part.equals("seq")) {
                String seq = parts[i + 1];
                return seq.substring(0,seq.length() - 1);
            }
        }
        return null;
    }

    static String ack(String[] parts) {
        for (int i=0; i<parts.length; i++) {
            String part = parts[i];
            if (part.equals("ack")) {
                String ack = parts[i + 1];
                return ack.substring(0,ack.length() - 1);
            }
        }
        return null;
    }

    static String flags(String[] parts) {
        for (int i=0; i<parts.length; i++) {
            String part = parts[i];
            if (part.equals("Flags")) {
                String flags = parts[i + 1];
                return flags.substring(1,flags.length() - 2);
            }
        }
        return null;
    }

    static String options(String[] parts) {
        for (int i=0; i<parts.length; i++) {
            String part = parts[i];
            if (part.equals("options")) {
                StringBuilder out = new StringBuilder();
                for (int j = i + 1; j<parts.length; j++) {
                    String opt = parts[j];
                    out.append(parts[j] + " ");
                    if (opt.endsWith("],")) {
                        break;
                    }
                }
                String options = out.toString();
                return options.substring(1,options.length() - 3);
            }
        }
        return null;
    }

    static int arrow(String[] parts) {
        boolean ipFound = false;
        for (int i=0; i<parts.length; i++) {
            String part = parts[i];
            if (ipFound && part.equals(">")) {
                return i;
            }
            if (part.startsWith("IP")) {
                ipFound = true;
            }
        }
        return -1;
    }

    static boolean isValid(String[] parts) {
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
        return "IP{" + source + " > " + destination + "}";
    }
}
