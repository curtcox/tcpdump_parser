import java.time.LocalTime;
import java.util.stream.*;
import java.io.*;

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
        return builder.build();
    }

    static Stream<Packet> parse(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines()
                .filter(line -> !line.trim().isEmpty())
                .map(line -> parse(line));
    }

    static Stream<Packet> parseValid(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines()
                .filter(line -> !line.trim().isEmpty())
                .filter(line -> !line.startsWith("\t0x"))
                .filter(line -> Parser.canParse(line))
                .map(line -> parse(line));
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
    private static Mac DA(String[] fields)          { return mac("DA",fields); }
    private static Mac RA(String[] fields)          { return mac("RA",fields); }
    private static Mac SA(String[] fields)          { return mac("SA",fields); }
    private static Mac TA(String[] fields)          { return mac("TA",fields); }
    private static String signal(String[] fields)   { return dB("signal",fields); }
    private static String noise(String[] fields)    { return dB("noise",fields); }
    private static Long duration(String[] fields)   {
        for (int i = 2; i<fields.length; i++) {
            if (fields[i].endsWith("us")) {
                String[] parts = fields[i].split("u");
                return Long.parseLong(parts[0]);
            }
        }
        return null;
    }

    private static Long offset(String[] fields)     {
        String[] parts = fields[1].split("u");
        return Long.parseLong(parts[0]);
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
                return Integer.parseInt(fields[i + 1]);
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

    public static void main(String[] args) {
        parse(System.in).forEach(packet -> System.out.println(packet));
    }
}
