import java.io.*;
import java.time.LocalTime;
import java.util.function.Supplier;

final class Parser {

    static Packet parse(UnparsedPacket unparsed) {
        try {
            return parse0(unparsed);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Unable to parse " + unparsed,e);
        }
    }

    static boolean canParse(UnparsedPacket unparsed) {
        try {
            return parse0(unparsed) != null;
        } catch (RuntimeException e) {
            System.err.println("Unable to parse " + unparsed);
            return false;
        }
    }

    private static Packet parse0(UnparsedPacket unparsed) {
        Fields fields     = Fields.of(unparsed.line);
        Packet.Builder builder = Packet.builder();
        builder.line      = unparsed.line;
        builder.localTime = Timestamp.parse(fields);
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
        builder.dump      = unparsed.dump;
        return builder.build();
    }

    static Packets parse(Supplier<InputStream> inputStream) {
        return Packets.of(() -> {
            return UnparsedPacketReader.of(inputStream).packets()
                    .map(line -> parse(line));
        });
    }

    private static Mac BSSID(Fields fields)   { return mac("BSSID",fields); }
    private static Mac RA(Fields fields)      { return mac("RA",fields); }
    private static Mac TA(Fields fields)      { return mac("TA",fields); }
    private static DB signal(Fields fields)   { return dB("signal",fields); }
    private static DB noise(Fields fields)    { return dB("noise",fields); }

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

    private static DB dB(String type, Fields fields)  {
        return DB.of(fields.directlyBefore(type));
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
        String value = fields.firstStartingWith(type);
        return value==null ? null : Mac.of(value.split(type + ":")[1]);
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
