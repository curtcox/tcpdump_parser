import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Main {

    final String[] args;

    Main(String[] args) {
        this.args = args;
    }

    static Main of(String[] args) {
        return new Main(args);
    }

    public static void main(String[] args) {
        Main main = new Main(args);
        Parser.parse(() -> System.in).forEach(packet -> System.out.println(packet));
    }

    Map<Mac,String> namesFromCommandLine() {
        Map<Mac,String> names = new HashMap<>();
        for (String arg : args) {
            String[] parts = arg.split("=");
            names.put(Mac.of(parts[0]),parts[1]);
        }
        return names;
    }

    static void forEachReliablePacket(Consumer<Packet> action) {
        Parser.parse(() -> System.in).reliable()
                .forEach(packet -> action.accept(packet));
    }
}
