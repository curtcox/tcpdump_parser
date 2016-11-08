import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TimelineTest {

    Packet ch1_conv1_p1 = parse("00:00:00.000000 0us IP 192.168.0.114.43114 > 10.9.8.7.https");
    Packet ch1_conv1_p2 = parse("00:00:00.000001 0us IP 10.9.8.7.https > 192.168.0.114.43114");
    Packet ch1_conv2_p1 = parse("00:00:00.000000 0us IP 192.168.0.114.8675 > 10.9.8.7.https");
    Packet ch1_conv2_p2 = parse("00:00:00.000001 0us IP 10.9.8.7.https > 192.168.0.114.8675");
    Packet ch2_conv1_p1 = parse("00:00:00.000001 0us IP 0.1.2.7.https > 192.168.0.114.43114");

    @Test
    public void timeline_of_no_packets() {
        Timeline timeline = timeline();
        assertEquals(timeline.channels.size(),0);
    }

    @Test
    public void timeline_of_1_packet() {
        Timeline timeline = timeline(ch1_conv1_p1);
        assertEquals(timeline.channels.size(),1);
        Channel channel = timeline.channels.get(0);
        assertEquals(channel.conversations.size(),1);
        assertSame(channel.conversations.get(0).packets.get(0), ch1_conv1_p1);
    }

    @Test
    public void timeline_of_2_packets_on_the_same_channel_from_same_conversation() {
        Timeline timeline = timeline(ch1_conv1_p1, ch1_conv1_p2);
        assertEquals(timeline.channels.size(),1);
        Channel channel = timeline.channels.get(0);
        assertEquals(channel.conversations.size(),1);
        List<Packet> packets = channel.conversations.get(0).packets;
        assertSame(packets.get(0), ch1_conv1_p1);
        assertSame(packets.get(1), ch1_conv1_p2);
    }

    @Test
    public void timeline_of_2_packets_on_the_same_channel_from_different_conversations() {
        Timeline timeline = timeline(ch1_conv1_p1, ch1_conv2_p1);
        assertEquals(timeline.channels.size(),1);
        Channel channel = timeline.channels.get(0);
        List<Conversation> conversations = channel.conversations;
        assertEquals(conversations.size(),2);
        assertEquals(conversations.get(0).packets.size(),1);
        assertEquals(conversations.get(1).packets.size(),1);
        assertSame(conversations.get(0).packets.get(0), ch1_conv1_p1);
        assertSame(conversations.get(1).packets.get(0), ch1_conv2_p1);
    }

    @Test
    public void timeline_of_4_packets_on_the_same_channel_from_different_conversations() {
        Timeline timeline = timeline(ch1_conv1_p1, ch1_conv1_p2, ch1_conv2_p1, ch1_conv2_p2);
        assertEquals(timeline.channels.size(),1);
        Channel channel = timeline.channels.get(0);
        assertEquals(channel.conversations.size(),2);
        assertEquals(channel.conversations.get(0).packets.size(),2);
        assertEquals(channel.conversations.get(1).packets.size(),2);
        assertSame(channel.conversations.get(0).packets.get(0), ch1_conv1_p1);
        assertSame(channel.conversations.get(0).packets.get(1), ch1_conv1_p2);
        assertSame(channel.conversations.get(1).packets.get(0), ch1_conv2_p1);
        assertSame(channel.conversations.get(1).packets.get(1), ch1_conv2_p2);
    }

    @Test
    public void timeline_of_2_packets_on_different_channels() {
        Timeline timeline = timeline(ch1_conv1_p1, ch2_conv1_p1);
        assertEquals(timeline.channels.size(),2);

        Channel channel1 = timeline.channels.get(0);
        assertEquals(channel1.conversations.size(),1);
        assertSame(channel1.conversations.get(0).packets.get(0), ch1_conv1_p1);

        Channel channel2 = timeline.channels.get(1);
        assertEquals(channel2.conversations.size(),1);
        assertSame(channel2.conversations.get(0).packets.get(0), ch2_conv1_p1);
    }

    static Timeline timeline(Packet... packets) {
        return Timeline.of(Packets.of(() -> Arrays.stream(packets)));
    }

    static Packet parse(String line) {
        return Parser.parse(UnparsedPacket.of(line));
    }
}
