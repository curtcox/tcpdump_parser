import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PacketsTest {

    @Test
    public void empty() {
        Packets packets = Packets.of(() -> Collections.EMPTY_LIST.stream());
        assertEquals(packets.count(),0);
        assertEquals(packets.allAccessPoints().count(),0);
        assertEquals(packets.allClients().size(),0);
        assertEquals(packets.allAccessPointVendors().count(),0);
    }
}
