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
        Parser.parseValid(input()).forEach(packet -> {
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
        Parser.parseValid(input()).forEach(packet -> {
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
