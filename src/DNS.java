final class DNS {

    final boolean query;
    final Host A;
    final Host CNAME;

    private DNS(boolean query, Host A, Host CNAME) {
        this.query = query;
        this.A = A;
        this.CNAME = CNAME;
    }

    static DNS parse(String[] parts) {
        if (valid(parts)) {
            return new DNS(query(parts),A(parts),CNAME(parts));
        }
        return null;
    }

    static boolean valid(String[] parts) {
        for (String part : parts) {
            if (part.equals("A?") || part.equals("CNAME")) {
                return true;
            }
        }
        return false;
    }

    private static boolean query(String[] parts) {
        for (String part : parts) {
            if (part.equals("A?")) {
                return true;
            }
        }
        return false;
    }

    private static Host A(String[] parts) {
        for (int i = 0; i<parts.length; i++) {
            if (parts[i].equals("A")) {
                return Host.of(parts[i + 1]);
            }
        }
        return null;
    }

    private static Host CNAME(String[] parts) {
        for (int i = 0; i<parts.length; i++) {
            if (parts[i].equals("A?") || parts[i].equals("CNAME")) {
                String name = parts[i +1];
                name = name.substring(0,name.lastIndexOf("."));
                return Host.of(name);
            }
        }
        return null;
    }

    public String toString() {
        return query
                ? String.format("DNS:{query CNAME=%s}",CNAME)
                : String.format("DNS:{CNAME=%s A=%s}", CNAME, A);
    }
}
