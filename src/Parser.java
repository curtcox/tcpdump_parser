import java.io.*;
import java.time.LocalTime;
import java.util.function.Supplier;

final class Parser {

    static Packet parse(String line) {
        try {
            return parse0(line);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Unable to parse " + line,e);
        }
    }

    static boolean canParse(String line) {
        try {
            return parse0(line) != null;
        } catch (RuntimeException e) {
            System.err.println("Unable to parse " + line);
            return false;
        }
    }

    private static Packet parse0(String line) {
        String[] fields = line.split(" ");
        Packet.Builder builder = Packet.builder();
        builder.line      = line;
        builder.localTime = localTime(fields);
        builder.BSSID     = BSSID(fields);
        builder.DA        = DA(fields);
        builder.RA        = RA(fields);
        builder.SA        = SA(fields);
        builder.TA        = TA(fields);
        builder.signal    = signal(fields);
        builder.noise     = noise(fields);
        builder.offset    = offset(fields);
        builder.duration  = duration(fields);
        builder.length    = length(fields);
        builder.radioTap  = RadioTap.parse(fields);
        builder.http      = HTTP.parse(fields);
        builder.ip        = IP.parse(fields);
        builder.type      = Type.parse(fields);
        return builder.build();
    }

    static Packets parse(Supplier<InputStream> inputStream) {
        return Packets.of(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream.get()));
            return reader.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .map(line -> parse(line));
        });
    }

    static Packets parseValid(Supplier<InputStream> inputStream) {
        return Packets.of(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream.get()));
            return reader.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.startsWith("\t0x"))
                    .filter(line -> Parser.canParse(line))
                    .map(line -> parse(line));
        });
    }

    private static LocalTime localTime(String[] fields) {
        String[] parts = fields[0].split(":");
        int hour   = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = Integer.parseInt(parts[2].split("\\.")[0]);
        int nano   = Integer.parseInt(parts[2].split("\\.")[1]) * 1000;
        return LocalTime.of(hour,minute,second,nano);
    }

    private static Mac BSSID(String[] fields)       { return mac("BSSID",fields); }
    private static Mac RA(String[] fields)          { return mac("RA",fields); }
    private static Mac TA(String[] fields)          { return mac("TA",fields); }
    private static String signal(String[] fields)   { return dB("signal",fields); }
    private static String noise(String[] fields)    { return dB("noise",fields); }

    private static Mac SA(String[] fields) {
        Mac SA = mac("SA",fields);
        if (SA != null) {
            return SA;
        }
        return macFrom(fields);
    }

    private static Mac DA(String[] fields) {
        Mac DA = mac("DA",fields);
        if (DA != null) {
            return DA;
        }
        return macTo(fields);
    }

    private static Microseconds duration(String[] fields)   {
        for (int i = 2; i<fields.length; i++) {
            if (Microseconds.canParse(fields[i])) {
                return Microseconds.parse(fields[i]);
            }
        }
        return null;
    }

    private static Microseconds offset(String[] fields)     {
        return Microseconds.parse(fields[1]);
    }

    private static String dB(String type, String[] fields)  {
        for (int i = 0; i<fields.length; i++) {
            if (fields[i].equals(type)) {
                return fields[i-1];
            }
        }
        return null;
    }

    private static Integer length(String[] fields)  {
        for (int i = 0; i<fields.length; i++) {
            if (fields[i].equals("length")) {
                String value = fields[i + 1];
                if (value.endsWith(":")) {
                    value = value.substring(0,value.length() - 1);
                }
                return Integer.parseInt(value);
            }
        }
        return null;
    }

    private static Mac mac(String type,String[] fields) {
        for (String field : fields) {
            if (field.startsWith(type)) {
                return Mac.of(field.split(type + ":")[1]);
            }
        }
        return null;
    }

    private static int arrow(String[] fields) {
        for (int i = 0; i< fields.length; i++) {
            if (fields[i].equals(">")) {
                return i;
            }
        }
        return -1;
    }

    private static Mac macFrom(String[] fields) {
        for (int i = arrow(fields) - 1; i>0; i--) {
            Mac mac = Mac.of(fields[i]);
            if (mac != null) {
                return mac;
            }
        }
        return null;
    }

    private static Mac macTo(String[] fields) {
        for (int i = arrow(fields) + 1; i<fields.length; i++) {
            Mac mac = Mac.of(fields[i]);
            if (mac != null) {
                return mac;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        parse(() -> System.in).forEach(packet -> System.out.println(packet));
    }
}
