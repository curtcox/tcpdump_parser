import org.junit.Test;

import java.io.*;

public class Demo {

    @Test
    public void demo() throws FileNotFoundException {
        FileInputStream in = new FileInputStream(new File("/tmp/tcpdump.txt"));
        Parser.parseValid(in).forEach(packet -> {
             System.out.println(packet);
        });
    }
}
