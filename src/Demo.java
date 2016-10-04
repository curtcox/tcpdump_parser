import org.junit.Test;

import java.io.*;
import java.util.*;

public class Demo {

    @Test
    public void demo() throws Exception {
        countAllMacs();
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
