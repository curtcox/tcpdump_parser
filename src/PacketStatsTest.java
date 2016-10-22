import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PacketStatsTest {

    @Test
    public void empty_stream() {
        PacketStats stats = PacketStats.fromPackets(Collections.EMPTY_LIST.stream());
        assertEquals(0,stats.bytes);
        assertEquals(0,stats.packets);
    }

    @Test
    public void null_length_is_treated_as_0_length_packet() {
        Packet.Builder builder = Packet.builder();
        int length = hashCode();
        Packet packet = builder.build();
        PacketStats stats = PacketStats.fromPackets(Collections.singleton(packet).stream());
        assertEquals(0,stats.bytes);
        assertEquals(1,stats.packets);
    }

    @Test
    public void stream_with_1_packet() {
        Packet.Builder builder = Packet.builder();
        int length = hashCode();
        builder.length = length;
        Packet packet = builder.build();
        PacketStats stats = PacketStats.fromPackets(Collections.singleton(packet).stream());
        assertEquals(length,stats.bytes);
        assertEquals(1,stats.packets);
    }

    @Test
    public void stream_with_2_packets() {
        Packet.Builder builder = Packet.builder();
        List<Packet> packets = new ArrayList<>();
        builder.length = hashCode();
        packets.add(builder.build());
        builder.length = builder.hashCode();
        packets.add(builder.build());

        PacketStats stats = PacketStats.fromPackets(packets.stream());

        int length = hashCode() + builder.hashCode();
        assertEquals(length,stats.bytes);
        assertEquals(2,stats.packets);
    }

}
