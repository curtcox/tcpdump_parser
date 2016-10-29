import java.util.*;

final class HTTP {

    final int length;
    final String verb;
    final String url;
    final Integer status;
    final boolean request;

    public HTTP(Builder builder) {
        length  = builder.length;
        verb    = builder.verb;
        url     = builder.url;
        status  = builder.status;
        request = verb != null;
    }

    static HTTP parse(Fields parts) {
        if (!isHTTP(parts)) {
            return null;
        }
        try {
            return parse0(parts);
        } catch (RuntimeException e) {
            return null;
        }
    }

    static boolean isHTTP(Fields parts) {
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

    private static HTTP parse0(Fields parts) {
        Builder builder = new Builder();
        int start = startIndex(parts);
        builder.length = parseLength(parts.at(start + 1));
        builder.verb = parseVerb(parts,start);
        builder.url = parseUrl(parts,start);
        builder.status = parseStatus(parts,start);
        return builder.build();
    }

    static int startIndex(Fields parts) {
        return parts.indexOf("length");
    }

    static int parseLength(String length) {
        return Integer.parseInt(length.substring(0,length.length() - 1));
    }

    static String parseVerb(Fields parts, int start) {
        int i = parts.indexOfAfter("HTTP:",start);
        if (i <0 || parts.at(i + 1).startsWith("HTTP")) {
            return null;
        }
        return parts.at(i+1);
    }

    static Integer parseStatus(Fields parts, int start) {
        for (int i = start; i < parts.length(); i++) {
            if (parts.at(i).startsWith("HTTP/")) {
                return parts.length() > i + 1 ? parseInt(parts.at(i + 1)) : null;
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

    static String parseUrl(Fields parts, int start) {
        int i = parts.indexOfAfter("HTTP:",start);
        if (i <0 || parts.at(i + 1).startsWith("HTTP")) {
            return null;
        }
        return parts.at(i+2);
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
