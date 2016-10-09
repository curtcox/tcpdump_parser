import java.util.*;
import java.util.stream.*;

final class Counter<K> {

    private final Map<K,Integer> tally = new HashMap();

    void add(K k) {
        if (k==null) {
            return;
        }
        Integer existing = tally.get(k);
        if (existing==null) {
            tally.put(k,1);
        } else {
            tally.put(k,existing + 1);
        }
    }

    Map<K,Integer> counts() {
        return tally.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
