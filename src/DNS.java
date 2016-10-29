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
        String name = parts.directlyAfter("A");
        return name == null ? null : Host.of(name);
    }

    private static Host CNAME(Fields parts) {
        String name = parts.directlyAfterEither("A?","CNAME");
        return name == null ? null : Host.of(name.substring(0,name.lastIndexOf(".")));
    }

    public String toString() {
        return query
                ? String.format("DNS:{query CNAME=%s}",CNAME)
                : String.format("DNS:{CNAME=%s A=%s}", CNAME, A);
    }
}
