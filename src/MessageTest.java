import org.junit.Test;

import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;

public class MessageTest {

    LocalTime localTime = LocalTime.now();
    IP ip = ip("IP 1.2.3.4.43114 > 5.6.7.8.https");
    IP publicToPrivate = ip("IP 5.6.7.9.http > 192.168.3.4.43114");

    static IP ip(String string) {
        return IP.parse(string.split(" "));
    }

    @Test
    public void empty() {
        List<Packet> packets = Collections.EMPTY_LIST;
        List<Message> messages = Message.messagesFromPackets(packets);

        assertEquals(0,messages.size());
    }

    @Test
    public void messages_of_1_outgoing_packet_has_the_proper_counts() {
        Packet.Builder builder = Packet.builder();
        int length = hashCode();
        builder.length = length;
        builder.ip = ip;
        Message message = oneMessageFrom(builder);

        assertEquals(1,message.packets.size());
        assert(message.stats.packets == 1);
        assert(message.stats.bytes == length);
    }

    @Test
    public void messages_of_1_incoming_packet_has_the_proper_counts() {
        Packet.Builder builder = Packet.builder();
        int length = hashCode();
        builder.length = length;
        builder.ip = publicToPrivate;
        Message message = oneMessageFrom(builder);

        assertEquals(1,message.packets.size());
        assert(message.stats.packets == 1);
        assert(message.stats.bytes == length);
    }

    @Test
    public void messages_of_1_packet_contains_the_packet() {
        Packet.Builder builder = Packet.builder();
        builder.length = hashCode();
        builder.ip = ip;
        Packet packet = builder.build();
        List<Message> messages = Message.messagesFromPackets(Collections.singletonList(packet));
        assertEquals(1,messages.size());

        Message message = messages.get(0);
        assertEquals(1,message.packets.size());
        assertSame(message.packets.get(0),packet);
    }

    @Test
    public void message_of_1_packet_contains_the_begin_time() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        builder.localTime = localTime;
        Message message = oneMessageFrom(builder);

        assertSame(message.begin,localTime);
    }

    @Test
    public void message_of_1_packet_contains_the_end_time() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        builder.localTime = localTime;
        Message message = oneMessageFrom(builder);
        assertSame(message.end,localTime);
    }

    @Test
    public void message_of_2_packets_going_the_same_way_is_1_message() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        Packet packet1 = builder.build();
        Packet packet2 = builder.build();

        Conversation conversation = Conversation.of(packet1,packet2);
        List<Message> messages = conversation.messages;

        assertEquals(1,messages.size());
    }

    @Test
    public void message_of_2_packets_going_different_ways_is_2_messages() {
        Packet.Builder builder = Packet.builder();
        builder.ip = ip;
        Packet packet1 = builder.build();
        builder.ip = publicToPrivate;
        Packet packet2 = builder.build();

        Conversation conversation = Conversation.of(packet1,packet2);
        List<Message> messages = conversation.messages;

        assertEquals(2,messages.size());
    }

    @Test
    public void message_of_2_packets_contains_the_proper_begin_and_end_time() {
        Packet.Builder builder = Packet.builder();
        builder.localTime = LocalTime.MIN;
        builder.ip = ip;
        Packet packet1 = builder.build();
        builder.localTime = LocalTime.MAX;
        Packet packet2 = builder.build();

        Conversation conversation = Conversation.of(packet1,packet2);
        List<Message> messages = conversation.messages;
        Message message = messages.get(0);

        assertSame(message.begin,LocalTime.MIN);
        assertSame(message.end,LocalTime.MAX);
    }

    Message oneMessageFrom(Packet.Builder builder) {
        Packet packet = builder.build();
        List<Message> messages = Message.messagesFromPackets(Collections.singletonList(packet));
        assertEquals(1,messages.size());
        return messages.get(0);
    }
}
