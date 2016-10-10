import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class Demo {

    @Test
    public void demo() {
        topIpPaths(100);
        listIpConversations();
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
        packets().ipPathsToCounts().entrySet().stream().limit(count)
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
        return Parser.parseValid(input()).reliable().IP();
    }

    Supplier<InputStream> input() {
        return new Supplier<InputStream>() {
            @Override
            public InputStream get() {
                try {
                    return new FileInputStream(new File("/Users/curt.cox/tmp/capture1.txt"));
                } catch (FileNotFoundException e){
                    throw new RuntimeException(e);
                }
            }
        };
    }

    void print(Object object) {
        System.out.println(object);
    }
}
