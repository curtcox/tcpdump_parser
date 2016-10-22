import java.util.function.Consumer;
import java.util.stream.*;

final class PacketStats {

    final int packets;
    final int bytes;

    private PacketStats(int packets, int bytes) {
        this.packets = packets;
        this.bytes = bytes;
    }

    static PacketStats fromPackets(Stream<Packet> stream) {
        Counter counter = new Counter();
        stream.forEach(counter);
        return new PacketStats(counter.packets,counter.bytes);
    }

    private static class Counter implements Consumer<Packet> {
        int packets = 0;
        int bytes = 0;

        @Override
        public void accept(Packet packet) {
            packets++;
            bytes += packet.length == null ? 0 : packet.length;
        }
    }
}
