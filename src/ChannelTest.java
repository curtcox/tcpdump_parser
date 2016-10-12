import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class ChannelTest {

    LocalTime localTime = LocalTime.now();
    IP ip = IP.parse("IP 132.245.72.114.https > 10.33.44.33.43114".split(" "));

    @Test
    public void channel_of_no_packets() {
        Channel channel = Channel.of();
        assertEquals(channel.packets.size(),0);
    }

    @Test
    public void channel_of_1_packet_contains_the_packet() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet = builder.build();
        Channel channel = of(packet);

        assertEquals(channel.packets.size(),1);
        assertSame(channel.packets.get(0),packet);
    }

    @Test
    public void channel_of_1_packet_contains_the_client() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Channel channel = of(builder.build());
        Packet packet = channel.packets.get(0);
        assertEquals(channel.client,packet.ip.source.host);
    }

    @Test
    public void channel_of_1_packet_contains_the_server() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Channel channel = of(builder.build());
        Packet packet = channel.packets.get(0);
        assertEquals(channel.server,packet.ip.destination);
    }

    @Test
    public void channel_of_1_packet_contains_the_begin_time() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        builder.localTime = localTime;
        Channel channel = of(builder.build());
        Packet packet = channel.packets.get(0);
        assertSame(channel.begin,packet.localTime);
    }

    @Test
    public void channel_of_1_packet_contains_the_end_time() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        builder.localTime = localTime;
        Channel channel = of(builder.build());
        Packet packet = channel.packets.get(0);
        assertSame(channel.end,packet.localTime);
    }

    @Test
    public void throws_helpful_exception_when_missing_IP() {
        try {
            Packet.Builder builder = Packet.builder();
            builder.localTime = localTime;
            of(builder.build());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"IP missing, but required for timeline packets.");
        }
    }

    @Test
    public void throws_helpful_exception_when_missing_time() {
        try {
            of(Packet.builder().build());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"Time missing, but required for timeline packets.");
        }
    }

    @Test
    public void channel_of_2_packets_contains_the_packets() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet1 = builder.build();
        Packet packet2 = builder.build();
        Channel channel = of(packet1,packet2);

        assertEquals(channel.packets.size(),2);
        assertSame(channel.packets.get(0),packet1);
        assertSame(channel.packets.get(1),packet2);
    }

    @Test
    public void channel_of_2_packets_contains_the_proper_begin_and_end_time() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = LocalTime.MIN;
        builder.ip = ip;
        Packet packet1 = builder.build();
        builder.localTime = LocalTime.MAX;
        Packet packet2 = builder.build();

        Channel channel = of(packet1,packet2);

        assertSame(channel.begin,LocalTime.MIN);
        assertSame(channel.end,LocalTime.MAX);
    }

    @Test
    public void throws_helpful_exception_when_packets_added_out_of_order() {
        try {
            Packet.Builder builder = Packet.builder();
            builder.localTime = LocalTime.MAX;
            builder.ip = ip;
            Packet packet1 = builder.build();
            builder.localTime = LocalTime.MIN;
            Packet packet2 = builder.build();
            of(packet1,packet2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"Packets must be added in chronological order.");
        }
    }

    Channel of(Packet...packets) {
        return Channel.of(packets);
    }
}
