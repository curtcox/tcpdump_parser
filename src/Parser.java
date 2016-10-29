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
        Fields fields     = Fields.of(line);
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
        builder.dns       = DNS.parse(fields);
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

    private static LocalTime localTime(Fields fields) {
        String[] parts = fields.at(0).split(":");
        int hour   = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = Integer.parseInt(parts[2].split("\\.")[0]);
        int nano   = Integer.parseInt(parts[2].split("\\.")[1]) * 1000;
        return LocalTime.of(hour,minute,second,nano);
    }

    private static Mac BSSID(Fields fields)       { return mac("BSSID",fields); }
    private static Mac RA(Fields fields)          { return mac("RA",fields); }
    private static Mac TA(Fields fields)          { return mac("TA",fields); }
    private static String signal(Fields fields)   { return dB("signal",fields); }
    private static String noise(Fields fields)    { return dB("noise",fields); }

    private static Mac SA(Fields fields) {
        Mac SA = mac("SA",fields);
        if (SA != null) {
            return SA;
        }
        return macFrom(fields);
    }

    private static Mac DA(Fields fields) {
        Mac DA = mac("DA",fields);
        if (DA != null) {
            return DA;
        }
        return macTo(fields);
    }

    private static Microseconds duration(Fields fields)   {
        for (int i = 2; i<fields.length(); i++) {
            if (Microseconds.canParse(fields.at(i))) {
                return Microseconds.parse(fields.at(i));
            }
        }
        return null;
    }

    private static Microseconds offset(Fields fields)     {
        return Microseconds.parse(fields.at(1));
    }

    private static String dB(String type, Fields fields)  {
        return fields.directlyBefore(type);
    }

    private static Integer length(Fields fields)  {
        String value = fields.directlyAfter("length");
        if (value==null) {
            return null;
        }
        if (value.endsWith(":")) {
            value = value.substring(0,value.length() - 1);
        }
        return Integer.parseInt(value);
    }

    private static Mac mac(String type,Fields fields) {
        for (String field : fields) {
            if (field.startsWith(type)) {
                return Mac.of(field.split(type + ":")[1]);
            }
        }
        return null;
    }

    private static int arrow(Fields fields) {
        return fields.indexOf(">");
    }

    private static Mac macFrom(Fields fields) {
        for (int i = arrow(fields) - 1; i>0; i--) {
            Mac mac = Mac.of(fields.at(i));
            if (mac != null) {
                return mac;
            }
        }
        return null;
    }

    private static Mac macTo(Fields fields) {
        for (int i = arrow(fields) + 1; i<fields.length(); i++) {
            Mac mac = Mac.of(fields.at(i));
            if (mac != null) {
                return mac;
            }
        }
        return null;
    }

}
