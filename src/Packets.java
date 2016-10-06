import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class Packets {

    private final Stream<Packet> stream;

    private Packets(Stream<Packet> stream) {
        this.stream = stream;
    }

    static Packets of(Stream<Packet> stream) {
        return new Packets(stream);
    }

    Packets reliable() {
        return Packets.of(stream.filter(packet -> !packet.radioTap.badFcs));
    }

    void forEach(Consumer<Packet> action) {
        stream.forEach(action);
    }

    <R> Stream<R> map(Function<Packet, ? extends R> mapper) {
        return stream.map(mapper);
    }

    long count() {
        return stream.count();
    }

    Set<Mac> allMacs() {
        Set macs = new HashSet();
        stream.forEach(packet -> {
                    macs.add(packet.BSSID);
                    macs.add(packet.DA);
                    macs.add(packet.SA);
                    macs.add(packet.TA);
                    macs.add(packet.RA);
                });
        macs.remove(null);
        return new TreeSet(macs);
    }

    Map<Mac,Integer> macToCounts() {
        Counter macs = new Counter();
        stream.forEach(packet -> {
                    macs.add(packet.BSSID);
                    macs.add(packet.DA);
                    macs.add(packet.SA);
                    macs.add(packet.TA);
                    macs.add(packet.RA);
                });
        return macs.counts;
    }

    Stream<Mac> allAccessPoints() {
        return stream.map(packet -> packet.BSSID)
                .filter(x -> x!=null)
                .sorted()
                .distinct();
    }

    Stream<String> allAccessPointVendors() {
        return stream.map(packet -> packet.BSSID)
                .filter(x -> x!=null)
                .map(mac -> mac.vendor)
                .sorted()
                .distinct();
    }

    Stream<Mac> macsThatAppearMoreThanTimes(int time) {
        Map<Mac,Integer> counts = macToCounts();
        return allMacs().stream()
                .filter(m -> counts.get(m) > time);
    }

    Stream<String> topMacsByAppearances(int limit) {
        Map<Mac,Integer> counts = macToCounts();
        List<String> lines = allMacs().stream()
                .map(m -> String.format("%06d", counts.get(m)) + " -> " + m)
                .sorted()
                .collect(Collectors.toList());
        Collections.reverse(lines);
        return lines.stream().limit(limit);
    }

}