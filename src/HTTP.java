
final class HTTP {

    final int length;

    public HTTP(Builder builder) {
        length = builder.length;
    }

    static HTTP parse(String[] parts) {
        Builder builder = new Builder();
        int start = startIndex(parts);
        builder.length = parseLength(parts[start + 1]);
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

    static class Builder {
        int length;

        HTTP build() {
            return new HTTP(this);
        }
    }
}
