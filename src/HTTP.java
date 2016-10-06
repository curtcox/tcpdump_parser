
final class HTTP {

    final int length;
    final String verb;
    final String url;
    final Integer status;

    public HTTP(Builder builder) {
        length = builder.length;
        verb = builder.verb;
        url = builder.url;
        status = builder.status;
    }

    static HTTP parse(String[] parts) {
        Builder builder = new Builder();
        int start = startIndex(parts);
        builder.length = parseLength(parts[start + 1]);
        builder.verb = parseVerb(parts,start);
        builder.url = parseUrl(parts,start);
        builder.status = parseStatus(parts,start);
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

    static Integer parseStatus(String[] parts, int start) {
        for (int i = start; i < parts.length; i++) {
            if (parts[i].equals("HTTP/1.1")) {
                return parts.length > i + 1 ? parseInt(parts[i + 1]) : null;
            }
        }
        return null;
    }

    static Integer parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static String parseUrl(String[] parts, int start) {
        for (int i = start; i < parts.length; i++) {
            if (parts[i].equals("HTTP:") && !parts[i + 1].startsWith("HTTP")) {
                return parts[i+2];
            }
        }
        return null;
    }

    static class Builder {
        int length;
        Integer status;
        String verb;
        String url;

        HTTP build() {
            return new HTTP(this);
        }
    }
}
