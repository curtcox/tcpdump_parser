import java.util.Arrays;
import java.util.Iterator;

final class Fields implements Iterable<String> {

    private String[] parts;

    private Fields(String[] parts) {
        this.parts = parts;
    }

    static Fields of(String line) {
        return new Fields(line.split(" "));
    }

    String at(int index) {
        return parts[index];
    }

    int length() {
        return parts.length;
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(parts).iterator();
    }
}
