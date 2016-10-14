import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class Packets {

    private final Supplier<Stream<Packet>> supplier;

    private Packets(Supplier<Stream<Packet>> supplier) {
        this.supplier = supplier;
    }

    static Packets of(Supplier<Stream<Packet>> stream) {
        return new Packets(stream);
    }

    private Stream<Packet> stream() {
        return supplier.get();
    }

    Packets reliable() {
        return filter(packet -> !packet.radioTap.badFcs);
    }

    Packets IP() {
        return filter(packet -> packet.ip != null);
    }

    Packets HTTP() {
        return filter(packet -> packet.http != null);
    }

    Packets summarize() {
        return Packets.of(
                () -> map(packet -> {
                    Packet.Builder builder = Packet.builder();
                    builder.BSSID = packet.BSSID;
                    builder.RA = packet.RA;
                    builder.TA = packet.TA;
                    builder.DA = packet.DA;
                    builder.SA = packet.SA;
                    builder.ip = packet.ip;
                    builder.localTime = packet.localTime.truncatedTo(ChronoUnit.SECONDS);
                    return builder.build();
                }).distinct());
    }

    Packets filter(Predicate<Packet> predicate) {
        return Packets.of(() -> stream().filter(predicate));
    }

    Packets contains(Mac mac) {
        return Packets.of(() -> stream().filter(packet -> packet.contains(mac)));
    }

    Packets contains(Host host) {
        return Packets.of(() -> stream().filter(packet -> packet.contains(host)));
    }

    void forEach(Consumer<Packet> action) {
        stream().forEach(action);
    }

    <R> Stream<R> map(Function<Packet, ? extends R> mapper) {
        return stream().map(mapper);
    }

    long count() {
        return stream().count();
    }

    Set<Mac> allMacs() {
        Set<Mac> macs = new HashSet();
        stream().forEach(packet -> macs.addAll(packet.allMacs()) );
        return new TreeSet(macs);
    }

    Set<Host> allHosts() {
        Set<Host> hosts = new HashSet();
        stream().forEach(packet ->  {
            IP ip = packet.ip;
            if (ip!=null) {
                hosts.add(ip.source.host);
                hosts.add(ip.destination.host);
            }
        });
        return new TreeSet<Host>(hosts);
    }

    Set<Mac> allClients() {
        Set macs = allMacs();
        macs.removeAll(allAccessPoints().collect(Collectors.toList()));
        macs.removeAll(Mac.BROADCAST);
        return new TreeSet(macs);
    }

    Map<Mac,Integer> macToCounts() {
        Counter macs = new Counter();
        stream().forEach(packet -> {
                    macs.add(packet.BSSID);
                    macs.add(packet.DA);
                    macs.add(packet.SA);
                    macs.add(packet.TA);
                    macs.add(packet.RA);
                });
        return macs.counts();
    }

    Map<Packet,Integer> macPathsToCounts() {
        Counter macs = new Counter();
        stream().forEach(packet -> {
            Packet.Builder builder = Packet.builder();
            builder.BSSID = packet.BSSID;
            builder.DA = packet.DA;
            builder.SA = packet.SA;
            builder.TA = packet.TA;
            builder.RA = packet.RA;
            macs.add(builder.build());
        });
        return macs.counts();
    }

    Map<Packet,Integer> ipPathsToCounts() {
        Counter ips = new Counter();
        stream().forEach(packet -> {
            Packet.Builder builder = Packet.builder();
            builder.ip = packet.ip;
            ips.add(builder.build());
        });
        return ips.counts();
    }

    Stream<Mac> allAccessPoints() {
        return stream().map(packet -> packet.BSSID)
                .filter(x -> x!=null)
                .sorted()
                .distinct();
    }

    Stream<String> allAccessPointVendors() {
        return stream().map(packet -> packet.BSSID)
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

}
