import java.util.*;
import java.util.function.Predicate;


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

    String directlyBefore(String name) {
        for (int i = 0; i<length(); i++) {
            if (at(i).equals(name)) {
                return at(i - 1);
            }
        }
        return null;
    }

    String directlyAfter(String name) {
        for (int i = 0; i<length(); i++) {
            if (at(i).equals(name)) {
                return at(i + 1);
            }
        }
        return null;
    }

    String directlyAfterEither(String name1, String name2) {
        for (int i = 0; i<length(); i++) {
            String name = at(i);
            if (name.equals(name1) || name.equals(name2)) {
                return at(i + 1);
            }
        }
        return null;
    }

    int indexOf(String name) {
        for (int i=0; i<length(); i++) {
            if (at(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    int indexOfAfter(String name, int start) {
        for (int i=start; i<length(); i++) {
            if (at(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(parts).iterator();
    }
}
