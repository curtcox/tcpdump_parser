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
            for (Channel.Builder channelBuilder : channelBuilders) {
                if (channelBuilder.accepts(packet)) {
                    channelBuilder.add(packet);
                    return;
                }
            }
            Channel.Builder channelBuilder = Channel.builder();
            channelBuilders.add(channelBuilder);
            channelBuilder.add(packet);
        }

        private List<Channel> channels() {
            return channelBuilders.stream().map(b -> b.build()).sorted().collect(Collectors.toList());
        }

        Timeline build() {
            return new Timeline(this);
        }
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Channel channel : channels) {
            out.append(channel);
        }
        return out.toString();
    }
}
