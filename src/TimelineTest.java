import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class TimelineTest {

    Packet c1packet1 = parse("00:00:00.000000 0us IP 192.168.0.114.43114 > 10.9.8.7.https");
    Packet c1packet2 = parse("00:00:00.000001 0us IP 10.9.8.7.https > 192.168.0.114.43114");

    @Test
    public void timeline_of_no_packets() {
        Timeline timeline = timeline();
        assertEquals(timeline.channels.size(),0);
    }

    @Test
    public void timeline_of_1_packet() {
        Timeline timeline = timeline(c1packet1);
        assertEquals(timeline.channels.size(),1);
        Channel channel = timeline.channels.get(0);
        assertEquals(channel.packets.size(),1);
        assertSame(channel.packets.get(0), c1packet1);
    }

    @Test
    public void timeline_of_2_packets_on_the_same_channel() {
        Timeline timeline = timeline(c1packet1,c1packet2);
        assertEquals(timeline.channels.size(),1);
        Channel channel = timeline.channels.get(0);
        assertEquals(channel.packets.size(),2);
        assertSame(channel.packets.get(0), c1packet1);
        assertSame(channel.packets.get(1), c1packet2);
    }

    static Timeline timeline(Packet... packets) {
        return Timeline.of(Packets.of(() -> Arrays.stream(packets)));
    }

    static Packet parse(String line) {
        return Parser.parse(line);
    }
}
