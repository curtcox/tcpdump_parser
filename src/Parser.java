import java.time.LocalTime;
import java.util.*;

final class Parser {

    static Packet parse(String line) {
        String[] fields = line.split(" ");
        Packet.Builder builder = Packet.builder();
        builder.localTime = localTime(fields);
        builder.BSSID     = BSSID(fields);
        builder.DA        = DA(fields);
        builder.RA        = RA(fields);
        builder.SA        = SA(fields);
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

    private static Mac BSSID(String[] fields) { return mac("BSSID",fields); }
    private static Mac DA(String[] fields)    { return mac("DA",fields); }
    private static Mac RA(String[] fields)    { return mac("RA",fields); }
    private static Mac SA(String[] fields)    { return mac("SA",fields); }

    private static Mac mac(String type,String[] fields) {
        for (String field : fields) {
            if (field.startsWith(type)) {
                return Mac.of(field.split(type + ":")[1]);
            }
        }
        return null;
    }

}
