import java.util.*;
import java.util.stream.Collectors;

final class Timeline {

    final List<Channel> channels;

    private Timeline(Builder builder) {
        channels = builder.channels();
    }

    static Timeline of(Packets packets) {
        Builder builder = new Builder();
        packets.forEach(packet -> builder.add(packet));
        return builder.build();
    }

    static class Builder {

        private List<Channel.Builder> channelBuilders = new ArrayList<>();

        void add(Packet packet) {
            if (channelBuilders.isEmpty()) {
                Channel.Builder channel = Channel.builder();
                channelBuilders.add(channel);
            }
            channelBuilders.get(0).add(packet);
        }

        private List<Channel> channels() {
            return channelBuilders.stream().map(b -> b.build()).collect(Collectors.toList());
        }

        Timeline build() {
            return new Timeline(this);
        }
    }
}
