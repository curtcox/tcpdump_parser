import java.time.LocalTime;
import java.util.*;

final class Parser {

    static Packet parse(String line) {
        String[] fields = line.split(" ");
        return new Packet(localTime(fields),BSSID(fields));
    }

    private static LocalTime localTime(String[] fields) {
        String[] parts = fields[0].split(":");
        int hour   = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = Integer.parseInt(parts[2].split("\\.")[0]);
        int nano   = Integer.parseInt(parts[2].split("\\.")[1]) * 1000;
        return LocalTime.of(hour,minute,second,nano);
    }

    private static Mac BSSID(String[] fields) {
        for (String field : fields) {
            if (field.startsWith("BSSID")) {
                return Mac.of(field.split("BSSID:")[1]);
            }
        }
        return null;
    }
}
