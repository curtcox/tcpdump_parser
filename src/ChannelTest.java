import org.junit.Test;

import static org.junit.Assert.*;

public class ChannelTest {

    IP ip = IP.parse("IP 132.245.72.114.https > 10.33.44.33.43114".split(" "));

    @Test
    public void channel_of_no_packets() {
        Channel channel = Channel.of();
        assertEquals(channel.packets.size(),0);
    }

    @Test
    public void channel_of_one_packet_contains_the_packet() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        Packet packet = builder.build();
        Channel channel = of(packet);

        assertEquals(channel.packets.size(),1);
        assertSame(channel.packets.get(0),packet);
    }

    @Test
    public void channel_of_one_packet_contains_the_client() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        Channel channel = of(builder.build());
        Packet packet = channel.packets.get(0);
        assertEquals(channel.client,packet.ip.source.host);
    }

    @Test
    public void channel_of_one_packet_contains_the_server() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        Channel channel = of(builder.build());
        Packet packet = channel.packets.get(0);
        assertEquals(channel.server,packet.ip.destination);
    }

    Channel of(Packet...packets) {
        return Channel.of(packets);
    }
}
