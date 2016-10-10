import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class Demo {

    final String inputFile = "/tmp/capture.txt";
    final String outputFile = "/tmp/report.txt";
    PrintStream out = System.out;

    void writeToFile() {
        try {
            out = new PrintStream(new FileOutputStream(new File(outputFile)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void demo() {
        summarizeIpConversations();
    }

    void dumpAllPackets() {
        Parser.parseValid(input())
                .forEach(packet -> print(packet));
    }

    void networkQuality() {
        long all = Parser.parseValid(input()).count();
        long reliable = Parser.parseValid(input()).reliable().count();
        double percent = (reliable  * 100.0 ) / all;
        print(reliable + " / " + all + " or " + percent + "% OK");
    }

    void listAllMacs() throws Exception {
        print(allMacs());
    }

    void listConversations() {
        for (Mac mac : packets().allClients()) {
            packets().contains(mac).forEach(x -> print(x));
        }
    }

    void listIpConversations() {
        for (Mac mac : ipPackets().allClients()) {
            ipPackets().contains(mac).forEach(x -> print(x));
        }
    }

    void summarizeIpConversations() {
        for (Mac mac : summarizeIpPackets().allClients()) {
            summarizeIpPackets().map(packet -> packet.localTime + " " + packet.ip ).forEach(x -> print(x));
        }
    }

    void countAllMacs() {
        print(allMacs().size());
    }

    void topMacsByAppearances(int count) {
        packets().macToCounts().entrySet().stream().limit(count)
                .forEach(x -> print(x));
    }

    void topMacPaths(int count) {
        packets().macPathsToCounts().entrySet().stream().limit(count)
                .forEach(x -> print(x));
    }

    void topIpPaths(int count) {
        ipPackets().ipPathsToCounts().entrySet().stream().limit(count)
                .forEach(x -> print(x));
    }

    void allAccessPoints() {
        packets().allAccessPoints()
                .forEach(bssid -> print(bssid));
    }

    void allAccessPointVendors() {
        packets().allAccessPointVendors()
                .forEach(vendor -> print(vendor));
    }

    Set<Mac> allMacs() {
        return packets().allMacs();
    }

    Packets packets() {
        return Parser.parseValid(input()).reliable();
    }

    Packets ipPackets() {
        return packets().IP();
    }

    Packets summarizeIpPackets() {
        return ipPackets().summarize();
    }

    Supplier<InputStream> input() {
        return () -> {
            try {
                return new FileInputStream(new File(inputFile));
            } catch (FileNotFoundException e){
                throw new RuntimeException(e);
            }
        };
    }

    void print(Object object) {
        out.println(object);
    }
}
