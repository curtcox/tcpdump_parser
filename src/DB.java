import java.util.*;

final class DB implements Comparable {

    final String string;
    final int value;
    private static final Map<String, DB> dbs = new HashMap<>();

    private DB(String string, int value) {
        this.string = string;
        this.value = value;
    }

    static DB of(String string) {
        if (string==null || !string.endsWith("dB")) {
            return null;
        }
        return of0(string);
    }

    private static DB of0(String value) {
        if (dbs.containsKey(value)) {
            return dbs.get(value);
        }
        DB db = new DB(value, Integer.parseInt(value.substring(0,value.length() - 2)));
        dbs.put(value,db);
        return db;
    }

    @Override
    public boolean equals(Object o) {
        return o==this;
    }

    @Override public String toString() { return string; }
    @Override public int    hashCode() { return value;  }
    @Override public int compareTo(Object o) {
        DB that = (DB) o;
        return value - that.value;
    }
}
