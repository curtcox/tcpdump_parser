import org.junit.Test;

import java.time.*;
import java.util.*;

import static org.junit.Assert.*;

public class ChannelTest {

    LocalTime localTime = LocalTime.now();
    IP ip = ip("IP 1.2.3.4.43114 > 5.6.7.8.https");
    IP ipBack = ip("IP 5.6.7.8.https > 1.2.3.4.43114");
    IP differentClient = ip("IP 1.2.3.5.43114 > 5.6.7.8.https");
    IP differentClientPort = ip("IP 1.2.3.4.42960 > 5.6.7.8.https");
    IP differentServerHost = ip("IP 1.2.3.4.43114 > 5.6.7.9.https");
    IP differentServerPort = ip("IP 1.2.3.4.43114 > 5.6.7.8.http");
    IP differentServerHostAndPort = ip("IP 1.2.3.4.43114 > 5.6.7.9.http");
    IP privateToPublic = ip("IP 192.168.3.4.43114 > 5.6.7.9.http");
    IP publicToPrivate = ip("IP 5.6.7.9.http > 192.168.3.4.43114");

    static IP ip(String string) {
        return IP.parse(string.split(" "));
    }

    @Test
    public void channel_of_no_packets() {
        Channel channel = Channel.of();
        assertEquals(channel.conversations.size(),0);
        assertEquals(channel.incomingBytes,0);
        assertEquals(channel.incomingPackets,0);
        assertEquals(channel.outgoingBytes,0);
        assertEquals(channel.outgoingPackets,0);
    }

    @Test
    public void channels_compare_like_hosts() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet1 = builder.build();
        builder.ip = differentClient;
        Packet packet2 = builder.build();
        Channel channel1 = of(packet1);
        Channel channel2 = of(packet2);

        assertEquals(ip.source.host.compareTo(ip.source.host),channel1.compareTo(channel1));
        assertEquals(ip.source.host.compareTo(differentClient.source.host),channel1.compareTo(channel2));
        assertEquals(differentClient.source.host.compareTo(ip.source.host),channel2.compareTo(channel1));
    }

    @Test
    public void channel_of_1_packet_contains_the_packet() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet = builder.build();
        Channel channel = of(packet);

        assertEquals(channel.conversations.size(),1);
        assertSame(channel.conversations.get(0).packets.get(0),packet);
    }

    @Test
    public void channel_of_1_outgoing_packet_has_the_proper_counts() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.length = hashCode();
        builder.ip = ip;
        Packet packet = builder.build();
        Channel channel = of(packet);

        assertEquals(channel.incomingPackets,0);
        assertEquals(channel.incomingBytes,0);
        assertEquals(channel.outgoingPackets,1);
        assert(channel.outgoingBytes == packet.length);
    }

    @Test
    public void the_client_is_the_private_address_when_sending_happens_first() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = privateToPublic;
        Channel channel = of(builder.build());
        assertEquals(channel.client,privateToPublic.source.host);
    }

    @Test
    public void the_client_is_the_private_address_when_receiving_happens_first() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = publicToPrivate;
        Channel channel = of(builder.build());
        assertEquals(channel.client,publicToPrivate.destination.host);
        assertEquals(channel.server,publicToPrivate.source);
    }

    @Test
    public void channel_of_1_packet_contains_the_client() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Channel channel = of(builder.build());
        Packet packet = channel.conversations.get(0).packets.get(0);
        assertEquals(channel.client,packet.ip.source.host);
        assertEquals(channel.server,packet.ip.destination);
    }

    @Test
    public void channel_of_1_packet_contains_the_server() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Channel channel = of(builder.build());
        Packet packet = channel.conversations.get(0).packets.get(0);
        assertEquals(channel.server,packet.ip.destination);
    }

    @Test
    public void channel_of_1_packet_contains_the_begin_time() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        builder.localTime = localTime;
        Channel channel = of(builder.build());
        Packet packet = channel.conversations.get(0).packets.get(0);
        assertSame(channel.begin,packet.localTime);
    }

    @Test
    public void channel_of_1_packet_contains_the_end_time() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        builder.localTime = localTime;
        Channel channel = of(builder.build());
        Packet packet = channel.conversations.get(0).packets.get(0);
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
            assertEquals(e.getMessage(),"IP missing, but required for timeline conversations.");
        }
    }

    @Test
    public void throws_helpful_exception_when_missing_time() {
        try {
            of(Packet.builder().build());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"Time missing, but required for timeline conversations.");
        }
    }

    @Test
    public void channel_of_2_packets_going_the_same_way_contains_the_packets() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet1 = builder.build();
        Packet packet2 = builder.build();
        Channel channel = of(packet1,packet2);

        assertEquals(channel.conversations.size(),1);
        List<Packet> packets = channel.conversations.get(0).packets;
        assertEquals(packets.size(),2);
        assertSame(packets.get(0),packet1);
        assertSame(packets.get(1),packet2);
    }

    @Test
    public void channel_of_2_packets_going_different_ways_contains_the_packets() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet1 = builder.build();
        builder.ip = ipBack;
        Packet packet2 = builder.build();
        Channel channel = of(packet1,packet2);

        assertEquals(channel.conversations.size(),1);
        List<Packet> packets = channel.conversations.get(0).packets;
        assertEquals(packets.size(),2);
        assertSame(packets.get(0),packet1);
        assertSame(packets.get(1),packet2);
    }

    @Test
    public void channel_of_2_packets_in_different_conversations_contains_the_packets() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet1 = builder.build();
        builder.ip = differentClientPort;
        Packet packet2 = builder.build();
        Channel channel = of(packet1,packet2);

        assertEquals(channel.conversations.size(),2);
        assertEquals(channel.conversations.get(0).packets.size(),1);
        assertEquals(channel.conversations.get(1).packets.size(),1);
        assertSame(channel.conversations.get(0).packets.get(0),packet1);
        assertSame(channel.conversations.get(1).packets.get(0),packet2);
    }

    @Test
    public void channel_of_2_packets_going_different_ways_contains_the_endpoints() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet1 = builder.build();
        builder.ip = ipBack;
        Packet packet2 = builder.build();
        Channel channel = of(packet1,packet2);

        assertEquals(channel.client,packet1.ip.source.host);
        assertEquals(channel.server,packet1.ip.destination);
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

    @Test
    public void throws_helpful_exception_when_packets_added_with_different_clients() {
        try {
            Packet.Builder builder = Packet.builder();
            builder.localTime = localTime;
            builder.ip = ip;
            Packet packet1 = builder.build();
            builder.ip = differentClient;
            Packet packet2 = builder.build();
            of(packet1,packet2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"Packets in this channel must be between " + ip.source.host + " and " + ip.destination);
        }
    }

    @Test
    public void throws_helpful_exception_when_packets_added_with_different_server_hosts() {
        try {
            Packet.Builder builder = Packet.builder();
            builder.localTime = localTime;
            builder.ip = ip;
            Packet packet1 = builder.build();
            builder.ip = differentServerHost;
            Packet packet2 = builder.build();
            of(packet1,packet2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"Packets in this channel must be between " + ip.source.host + " and " + ip.destination);
        }
    }

    @Test
    public void throws_helpful_exception_when_packets_added_with_different_server_ports() {
        try {
            Packet.Builder builder = Packet.builder();
            builder.localTime = localTime;
            builder.ip = ip;
            Packet packet1 = builder.build();
            builder.ip = differentServerPort;
            Packet packet2 = builder.build();
            of(packet1,packet2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"Packets in this channel must be between " + ip.source.host + " and " + ip.destination);
        }
    }

    @Test
    public void throws_helpful_exception_when_packets_added_with_different_server_host_and_ports() {
        try {
            Packet.Builder builder = Packet.builder();
            builder.localTime = localTime;
            builder.ip = ip;
            Packet packet1 = builder.build();
            builder.ip = differentServerHostAndPort;
            Packet packet2 = builder.build();
            of(packet1,packet2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),"Packets in this channel must be between " + ip.source.host + " and " + ip.destination);
        }
    }

    @Test
    public void accepts_packets_between_same_client_and_server() {
        Packet.Builder packetBuilder = Packet.builder();
        packetBuilder.localTime = localTime;
        packetBuilder.ip = ip;
        Packet packet1 = packetBuilder.build();
        Packet packet2 = packetBuilder.build();
        packetBuilder.ip = ipBack;
        Packet packet3 = packetBuilder.build();
        Channel.Builder channelBuilder = Channel.builder();
        channelBuilder.add(packet1);

        assert(channelBuilder.accepts(packet2));
        assert(channelBuilder.accepts(packet3));
    }

    @Test
    public void rejects_packets_between_different_clients_and_servers() {
        Packet.Builder packetBuilder = Packet.builder();
        packetBuilder.localTime = localTime;
        packetBuilder.ip = ip;
        Packet packet1 = packetBuilder.build();
        packetBuilder.ip = differentServerHost;
        Packet packet2 = packetBuilder.build();
        packetBuilder.ip = differentServerPort;
        Packet packet3 = packetBuilder.build();
        packetBuilder.ip = differentServerHostAndPort;
        Packet packet4 = packetBuilder.build();
        Channel.Builder channelBuilder = Channel.builder();
        channelBuilder.add(packet1);

        assertFalse(channelBuilder.accepts(packet2));
        assertFalse(channelBuilder.accepts(packet3));
        assertFalse(channelBuilder.accepts(packet4));
    }

    Channel of(Packet...packets) {
        return Channel.of(packets);
    }
}
