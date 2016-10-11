import java.util.ArrayList;
import java.util.List;

final class Timeline {

    private Timeline(Builder builder) {

    }

    static Timeline of(Packets packets) {
        Builder builder = new Builder();
        packets.forEach(packet -> builder.add(packet));
        return builder.build();
    }

    List<Channel> channels() {
        return new ArrayList<>();
    }

    static class Builder {

        void add(Packet packet) {

        }

        Timeline build() {
            return new Timeline(this);
        }
    }
}
