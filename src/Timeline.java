import java.util.*;

final class Timeline {

    final List<Channel> channels;

    private Timeline(Builder builder) {
        channels = Collections.unmodifiableList(builder.channels);
    }

    static Timeline of(Packets packets) {
        Builder builder = new Builder();
        packets.forEach(packet -> builder.add(packet));
        return builder.build();
    }

    static class Builder {

        private List<Channel> channels = new ArrayList<>();

        void add(Packet packet) {
            Channel.Builder channel = Channel.builder();
            channel.add(packet);
            channels.add(channel.build());
        }

        Timeline build() {
            return new Timeline(this);
        }
    }
}
