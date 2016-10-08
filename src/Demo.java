import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Supplier;

public class Demo {

    @Test
    public void demo() throws Exception {
        topMacsByAppearances();
    }

    void dumpAllPackets() throws Exception {
        Parser.parseValid(input())
                .forEach(packet -> print(packet));
    }

    void networkQuality() throws Exception {
        long all = Parser.parseValid(input()).count();
        long reliable = Parser.parseValid(input()).reliable().count();
        double percent = (reliable  * 100.0 ) / all;
        print(reliable + " / " + all + " or " + percent + "% OK");
    }

    void listAllMacs() throws Exception {
        print(allMacs());
    }

    void countAllMacs() throws Exception {
        print(allMacs().size());
    }

    void topMacsByAppearances() throws Exception {
        packets().topMacsByAppearances(100)
                .forEach(x -> print(x));
    }

    void allAccessPoints() throws Exception {
        packets().allAccessPoints()
                .forEach(bssid -> print(bssid));
    }

    void allAccessPointVendors() throws Exception {
        packets().allAccessPointVendors()
                .forEach(vendor -> print(vendor));
    }

    Set<Mac> allMacs() throws Exception {
        return packets().allMacs();
    }

    Packets packets() throws Exception {
        return Parser.parseValid(input()).reliable();
    }

    Supplier<InputStream> input() {
        return new Supplier<InputStream>() {
            @Override
            public InputStream get() {
                try {
                    return new FileInputStream(new File("/tmp/tcpdump.txt"));
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
