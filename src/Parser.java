import java.time.LocalTime;
import java.util.*;

final class Parser {

    static Packet parse(String line) {
        String[] fields = line.split(" ");
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime(fields);
        builder.BSSID     = BSSID(fields);
        builder.DA        = DA(fields);
        return builder.build();
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

    private static Mac DA(String[] fields) {
        for (String field : fields) {
            if (field.startsWith("DA")) {
                return Mac.of(field.split("DA:")[1]);
            }
        }
        return null;
    }

}
