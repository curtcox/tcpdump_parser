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

        boolean summary(String line) {
            return  line != null &&
                    !line.trim().isEmpty() &&
                    !line.startsWith("\t0x") &&
                    Parser.canParse(UnparsedPacket.of(line,dump));
        }

        void readNext() {
            try {
                nextLine = reader.readLine();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public boolean hasNext() {
            if (nextLine != null) {
                return true;
            } else {
                readNext();
                while (nextLine != null && !summary(nextLine)) {
                    readNext();
                }
                return summary(nextLine);
            }
        }


        @Override
        public UnparsedPacket next() {
            if (summary(nextLine) || hasNext()) {
                return constructPacket();
            } else {
                throw new NoSuchElementException();
            }
        }

        UnparsedPacket constructPacket() {
            UnparsedPacket packet = UnparsedPacket.of(nextLine,dump);
            nextLine = null;
            return packet;
        }
    }
}
