import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TimelineTest {

    Packet packet1 = parse("00:00:00.000000 0us IP 192.168.0.114.43114 > 10.9.8.7.https");

    @Test
    public void timeline_of_no_packets() {
        Timeline timeline = Timeline.of(Packets.of(() -> Collections.EMPTY_LIST.stream()));
        assertEquals(timeline.channels().size(),0);
    }

    static Packet parse(String line) {
        return Parser.parse(line);
    }
}
