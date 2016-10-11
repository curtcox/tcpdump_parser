import java.util.*;

final class Channel {

    final List<Packet> packets;

    private Channel(Builder builder) {
        packets = Collections.unmodifiableList(builder.packets);
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private List<Packet> packets = new ArrayList<>();

        Channel build() {
            return new Channel(this);
        }

        public void add(Packet packet) {
            packets.add(packet);
        }
    }
}
