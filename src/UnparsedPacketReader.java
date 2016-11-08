import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class UnparsedPacketReader {

    final BufferedReader reader;

    private UnparsedPacketReader(Supplier<InputStream> inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream.get()));
    }

    static UnparsedPacketReader of(Supplier<InputStream> inputStream) {
        return new UnparsedPacketReader(inputStream);
    }

    Stream<UnparsedPacket> packets() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator(), Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    UnparsedPacketIterator iterator() {
        return new UnparsedPacketIterator();
    }

    class UnparsedPacketIterator implements Iterator<UnparsedPacket> {
        String nextLine = null;
        String[] dump = new String[0];

        @Override
        public boolean hasNext() {
            if (summary(nextLine)) {
                return true;
            } else {
                try {
                    nextLine = reader.readLine();
                    return summary(nextLine);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        boolean summary(String line) {
            return   line != null &&
                    !line.trim().isEmpty() &&
                    !line.startsWith("\t0x") &&
                    Parser.canParse(UnparsedPacket.of(line,dump));
        }

        @Override
        public UnparsedPacket next() {
            if (summary(nextLine) || hasNext()) {
                String line = nextLine;
                nextLine = null;
                return UnparsedPacket.of(line,dump);
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
