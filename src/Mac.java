import java.util.*;

final class Mac implements Comparable {

    final static Mac all0 = new Mac("00:00:00:00:00:00",-1);
    final static Mac allf = new Mac("ff:ff:ff:ff:ff:ff",-2);
    final static Set<Mac> BROADCAST = new HashSet(Arrays.asList(new Mac[]{all0,allf}));
    final String value;
    final String vendor;
    final String serial;
    final int hash;
    private static final Map<String,Mac> macs = new HashMap<>();

    private Mac(String value, int hash) {
        this.value = value;
        this.hash = hash;
        vendor = value.substring(0,8);
        serial = value.substring(9);
    }

    static Mac of(String value) {
        if (value.split(":").length != 6) {
            return null;
        }
        return of0(value);
    }

    private static Mac of0(String value) {
        if (macs.containsKey(value)) {
            return macs.get(value);
        }
        Mac mac = new Mac(value,macs.size());
        macs.put(value,mac);
        return mac;
    }

    @Override
    public boolean equals(Object o) {
        return o==this;
    }

    @Override public String toString() { return value; }
    @Override public int    hashCode() { return hash;  }
    @Override public int compareTo(Object o) {
        Mac that = (Mac) o;
        return value.compareTo(that.value);
    }
}
