import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.*;

final class Packets {

    private final Stream<Packet> stream;

    private Packets(Stream<Packet> stream) {
        this.stream = stream;
    }

    static Packets of(Stream<Packet> stream) {
        return new Packets(stream);
    }

    Packets reliable() {
        return Packets.of(stream.filter(packet -> !packet.radioTap.badFcs));
    }

    void forEach(Consumer<Packet> action) {
        stream.forEach(action);
    }

    <R> Stream<R> map(Function<Packet, ? extends R> mapper) {
        return stream.map(mapper);
    }

    long count() {
        return stream.count();
    }
}
