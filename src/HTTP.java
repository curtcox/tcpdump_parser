
final class HTTP {

    final int length;
    final String verb;

    public HTTP(Builder builder) {
        length = builder.length;
        verb = builder.verb;
    }

    static HTTP parse(String[] parts) {
        Builder builder = new Builder();
        int start = startIndex(parts);
        builder.length = parseLength(parts[start + 1]);
        builder.verb = parseVerb(parts,start);
        return builder.build();
    }

    static int startIndex(String[] parts) {
        for (int i=0; i<parts.length; i++) {
            if (parts[i].equals("length")) {
                return i;
            }
        }
        return -1;
    }

    static int parseLength(String length) {
        return Integer.parseInt(length.substring(0,length.length() - 1));
    }

    static String parseVerb(String[] parts, int start) {
        for (int i = start; i < parts.length; i++) {
            if (parts[i].equals("HTTP:") && !parts[i + 1].startsWith("HTTP")) {
                return parts[i+1];
            }
        }
        return null;
    }

    static class Builder {
        int length;
        String verb;

        HTTP build() {
            return new HTTP(this);
        }
    }
}
