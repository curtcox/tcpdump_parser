final class DNS {

    final boolean query;
    final Host A;
    final Host CNAME;

    private DNS(boolean query, Host A, Host CNAME) {
        this.query = query;
        this.A = A;
        this.CNAME = CNAME;
    }

    static DNS parse(Fields parts) {
        if (valid(parts)) {
            return new DNS(query(parts),A(parts),CNAME(parts));
        }
        return null;
    }

    static boolean valid(Fields parts) {
        for (String part : parts) {
            if (part.equals("A?") || part.equals("CNAME")) {
                return true;
            }
        }
        return false;
    }

    private static boolean query(Fields parts) {
        for (String part : parts) {
            if (part.equals("A?")) {
                return true;
            }
        }
        return false;
    }

    private static Host A(Fields parts) {
        for (int i = 0; i<parts.length(); i++) {
            if (parts.at(i).equals("A")) {
                return Host.of(parts.at(i + 1));
            }
        }
        return null;
    }

    private static Host CNAME(Fields parts) {
        for (int i = 0; i<parts.length(); i++) {
            if (parts.at(i).equals("A?") || parts.at(i).equals("CNAME")) {
                String name = parts.at(i +1);
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
