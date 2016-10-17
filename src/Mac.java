import java.util.*;

final class Mac implements Comparable {

    final static Mac all0 = new Mac("00:00:00:00:00:00");
    final static Mac allf = new Mac("ff:ff:ff:ff:ff:ff");
    final static Set<Mac> BROADCAST = new HashSet(Arrays.asList(new Mac[]{all0,allf}));
    final String value;
    final String vendor;
    final String serial;

    private Mac(String value) {
        this.value = value;
        vendor = value.substring(0,8);
        serial = value.substring(9);
    }

    static Mac of(String value) {
        if (value.split(":").length != 6) {
            return null;
        }
        return new Mac(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o==null) {
            return false;
        }
        Mac that = (Mac) o;
        return value.equals(that.value);
    }

    @Override public String toString() { return value;            }
    @Override public int    hashCode() { return value.hashCode(); }
    @Override public int compareTo(Object o) {
        Mac that = (Mac) o;
        return value.compareTo(that.value);
    }
}
