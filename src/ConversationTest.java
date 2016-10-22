import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class ConversationTest {

    LocalTime localTime = LocalTime.now();
    IP ip = ip("IP 1.2.3.4.43114 > 5.6.7.8.https");
    IP ipBack = ip("IP 5.6.7.8.https > 1.2.3.4.43114");
    IP differentClientPort = ip("IP 1.2.3.4.42960 > 5.6.7.8.https");
    IP privateToPublic = ip("IP 192.168.3.4.43114 > 5.6.7.9.http");
    IP publicToPrivate = ip("IP 5.6.7.9.http > 192.168.3.4.43114");

    static IP ip(String string) {
        return IP.parse(string.split(" "));
    }

    @Test
    public void conversation_of_no_packets() {
        Conversation conversation = Conversation.of();
        assertEquals(conversation.packets.size(),0);
        assertEquals(conversation.incoming.bytes,0);
        assertEquals(conversation.outgoing.bytes,0);
        assertEquals(conversation.incoming.packets,0);
        assertEquals(conversation.outgoing.packets,0);
    }

    @Test
    public void conversation_of_1_outgoing_packet_has_the_proper_counts() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.length = hashCode();
        builder.ip = ip;
        Packet packet = builder.build();
        Conversation conversation = Conversation.of(packet);

        assertEquals(0,conversation.incoming.packets);
        assertEquals(0,conversation.incoming.bytes);
        assertEquals(1,conversation.outgoing.packets);
        assert(conversation.outgoing.bytes == packet.length);
    }

    @Test
    public void conversation_of_1_incoming_packet_has_the_proper_counts() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.length = hashCode();
        builder.ip = publicToPrivate;
        Packet packet = builder.build();
        Conversation conversation = Conversation.of(packet);

        assertEquals(1,conversation.incoming.packets);
        assert(conversation.incoming.bytes == packet.length);
        assertEquals(0,conversation.outgoing.packets);
        assertEquals(0,conversation.outgoing.bytes);
    }

    @Test
    public void conversation_of_1_packet_contains_the_packet() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Packet packet = builder.build();
        Conversation conversation = Conversation.of(packet);

        assertEquals(conversation.packets.size(),1);
        assertSame(conversation.packets.get(0),packet);
    }

    @Test
    public void the_client_is_the_private_address_when_sending_happens_first() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = privateToPublic;
        Conversation conversation = Conversation.of(builder.build());
        assertEquals(conversation.client,privateToPublic.source);
    }

    @Test
    public void the_client_is_the_private_address_when_receiving_happens_first() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = publicToPrivate;
        Conversation conversation = Conversation.of(builder.build());
        assertEquals(conversation.client,publicToPrivate.destination);
        assertEquals(conversation.server,publicToPrivate.source);
    }

    @Test
    public void conversation_of_1_packet_contains_the_client() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Conversation conversation = Conversation.of(builder.build());
        Packet packet = conversation.packets.get(0);
        assertEquals(conversation.client,packet.ip.source);
        assertEquals(conversation.server,packet.ip.destination);
    }

    @Test
    public void conversation_of_1_packet_contains_the_server() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime;
        builder.ip = ip;
        Conversation conversation = Conversation.of(builder.build());
        Packet packet = conversation.packets.get(0);
        assertEquals(conversation.server,packet.ip.destination);
    }

    @Test
    public void conversation_of_1_packet_contains_the_begin_time() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        builder.localTime = localTime;
        Conversation conversation = Conversation.of(builder.build());
        Packet packet = conversation.packets.get(0);
        assertSame(conversation.begin,packet.localTime);
    }

    @Test
    public void conversation_of_1_packet_contains_the_end_time() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        builder.localTime = localTime;
        Conversation conversation = Conversation.of(builder.build());
        Packet packet = conversation.packets.get(0);
        assertSame(conversation.end,packet.localTime);
    }

    @Test
    public void conversation_of_2_packets_contains_the_proper_begin_and_end_time() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = LocalTime.MIN;
        builder.ip = ip;
        Packet packet1 = builder.build();
        builder.localTime = LocalTime.MAX;
        Packet packet2 = builder.build();

        Conversation conversation = Conversation.of(packet1,packet2);

        assertSame(conversation.begin,LocalTime.MIN);
        assertSame(conversation.end,LocalTime.MAX);
    }

    @Test
    public void accepts_packets_between_same_client_and_server_on_the_same_port() {
        Packet.Builder packetBuilder = Packet.builder();
        packetBuilder.localTime = localTime;
        packetBuilder.ip = ip;
        Packet packet1 = packetBuilder.build();
        Packet packet2 = packetBuilder.build();
        packetBuilder.ip = ipBack;
        Packet packet3 = packetBuilder.build();
        Conversation.Builder conversationBuilder = Conversation.builder();
        conversationBuilder.add(packet1);

        assert(conversationBuilder.accepts(packet2));
        assert(conversationBuilder.accepts(packet3));
    }

    @Test
    public void rejects_packets_between_the_same_client_and_server_on_different_ports() {
        Packet.Builder packetBuilder = Packet.builder();
        packetBuilder.localTime = localTime;
        packetBuilder.ip = ip;
        Packet packet1 = packetBuilder.build();
        packetBuilder.ip = differentClientPort;
        Packet packet2 = packetBuilder.build();
        Conversation.Builder conversationBuilder = Conversation.builder();
        conversationBuilder.add(packet1);

        assertFalse(conversationBuilder.accepts(packet2));
    }

}
