import java.time.LocalTime;
import java.util.*;

final class Packet {

    final LocalTime localTime;
    final Mac BSSID;
    final Mac DA;
    final Mac RA;
    final Mac SA;
    final Mac TA;

    private Packet(Builder builder) {
        localTime = builder.localTime;
        BSSID     = builder.BSSID;
        DA        = builder.DA;
        RA        = builder.RA;
        SA        = builder.SA;
        TA        = builder.TA;
    }

    static class Builder {
        LocalTime localTime;
        Mac BSSID;
        Mac DA;
        Mac RA;
        Mac SA;
        Mac TA;

        Packet build() {
            return new Packet(this);
        }
    }

    static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        Map map = new TreeMap();
        if (localTime != null) { map.put("localTime", localTime.toString());}
        if (BSSID != null)     { map.put("BSSID",BSSID); }
        if (SA != null)        { map.put("SA",SA); }
        if (TA != null)        { map.put("TA",TA); }
        if (RA != null)        { map.put("RA",RA); }
        if (DA != null)        { map.put("DA",DA); }
        return "Packet:" + map;
    }
}
