import java.util.Objects;

final class TCP {

    final String seq;
    final String ack;
    final String flags;
    final String options;

    private TCP(Builder builder) {
        this.seq = builder.seq;
        this.ack = builder.ack;
        this.flags = builder.flags;
        this.options = builder.options;
    }

    static TCP parse(String[] parts) {
        if (!validTCP(parts)) {
            return null;
        }
        return parse0(parts);
    }

    private static TCP parse0(String[] parts) {
        Builder builder = new Builder();
        builder.seq = seq(parts);
        builder.ack = ack(parts);
        builder.flags = flags(parts);
        builder.options = options(parts);
        return builder.build();
    }

    static boolean validTCP(String[] parts) {
        for (String part : parts) {
            if (part.equals("Flags")) {
                return true;
            }
        }
        return false;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        String seq;
        String ack;
        String flags;
        String options;

        TCP build() {
            return new TCP(this);
        }
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

    @Override
    public boolean equals(Object o) {
        TCP that = (TCP) o;
        return  Objects.equals(flags,that.flags) &&
                Objects.equals(options,that.options) &&
                Objects.equals(seq,that.seq) &&
                Objects.equals(ack,that.ack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flags,options,seq,ack);
    }
}
