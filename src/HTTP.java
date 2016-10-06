import java.util.*;

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
        if (!isHTTP(parts)) {
            return null;
        }
        try {
            return parse0(parts);
        } catch (RuntimeException e) {
            return null;
        }
    }

    static boolean isHTTP(String[] parts) {
        boolean lengthFound = false;
        for (String part : parts) {
            if (lengthFound && part.startsWith("HTTP")) {
                return true;
            }
            if (part.equals("length")) {
                lengthFound = true;
            }
        }
        return false;
    }

    private static HTTP parse0(String[] parts) {
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
            if (parts[i].startsWith("HTTP/")) {
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

    @Override
    public String toString() {
        Map map = new TreeMap();
        map.put("length",length);
        if (verb   != null) { map.put("verb",  verb);   }
        if (url    != null) { map.put("url",   url);    }
        if (status != null) { map.put("status",status); }
        return "HTTP:" + map;
    }

}
