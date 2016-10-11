import org.junit.Test;

import static org.junit.Assert.*;

public class ChannelTest {

    @Test
    public void channel_of_no_packets() {
        Channel channel = Channel.builder().build();
        assertEquals(channel.packets.size(),0);
    }

    @Test
    public void channel_of_one_packet() {
        Channel.Builder builder = new Channel.Builder();
        Packet packet = Packet.builder().build();
        builder.add(packet);
        Channel channel = builder.build();

        assertEquals(channel.packets.size(),1);
        assertSame(channel.packets.get(0),packet);
    }

}
