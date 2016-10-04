import java.util.*;

class Counter<K> {

    final Map<K,Integer> counts = new HashMap();

    void add(K k) {
        if (k==null) {
            return;
        }
        Integer existing = counts.get(k);
        if (existing==null) {
            counts.put(k,1);
        } else {
            counts.put(k,existing + 1);
        }
    }
}
