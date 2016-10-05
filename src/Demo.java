import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Demo {

    @Test
    public void demo() throws Exception {
        topMacsByAppearances(100);
    }

    void dumpAllPackets() throws Exception {
        Parser.parseValid(input()).forEach(packet -> {
            print(packet);
        });
    }

    void listAllMacs() throws Exception {
        print(allMacs());
    }

    void countAllMacs() throws Exception {
        print(allMacs().size());
    }

    void allAccessPoints() throws Exception {
        Parser.parseReliable(input())
                .map(packet -> packet.BSSID)
                .filter(x -> x!=null)
                .sorted()
                .distinct()
                .forEach(bssid -> {
                    print(bssid);
                });
    }

    void allAccessPointVendors() throws Exception {
        Parser.parseReliable(input())
                .map(packet -> packet.BSSID)
                .filter(x -> x!=null)
                .map(mac -> mac.vendor)
                .sorted()
                .distinct()
                .forEach(vendor -> {
                    print(vendor);
                });
    }

    void macsThatAppearMoreThanTimes(int time) throws Exception {
        Map<Mac,Integer> counts = macToCounts();
        List<String> lines = allMacs().stream()
            .filter(m -> counts.get(m) > time)
            .map(m -> String.format("%06d", counts.get(m)) + " -> " + m)
            .sorted()
            .collect(Collectors.toList());
        Collections.reverse(lines);
        for (String line : lines) {
            print(line);
        }
    }

    void topMacsByAppearances(int limit) throws Exception {
        Map<Mac,Integer> counts = macToCounts();
        List<String> lines = allMacs().stream()
                .map(m -> String.format("%06d", counts.get(m)) + " -> " + m)
                .sorted()
                .collect(Collectors.toList());
        Collections.reverse(lines);
        lines.stream().limit(limit).forEach(x -> print(x));
    }

    Map<Mac,Integer> macToCounts() throws Exception {
        Counter macs = new Counter();
        Parser.parseReliable(input())
                .forEach(packet -> {
                    macs.add(packet.BSSID);
                    macs.add(packet.DA);
                    macs.add(packet.SA);
                    macs.add(packet.TA);
                    macs.add(packet.RA);
                });
        return macs.counts;
    }

    Set<Mac> allMacs() throws Exception {
        Set macs = new HashSet();
        Parser.parseValid(input())
                .forEach(packet -> {
                    macs.add(packet.BSSID);
                    macs.add(packet.DA);
                    macs.add(packet.SA);
                    macs.add(packet.TA);
                    macs.add(packet.RA);
                });
        macs.remove(null);
        return new TreeSet(macs);
    }

    InputStream input() throws FileNotFoundException {
        return new FileInputStream(new File("/tmp/tcpdump.txt"));
    }

    void print(Object object) {
        System.out.println(object);
    }
}
