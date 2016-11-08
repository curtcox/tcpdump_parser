import java.util.*;

final class Packet {

    final Timestamp timestamp;
    final Mac BSSID;
    final Mac DA;
    final Mac RA;
    final Mac SA;
    final Mac TA;
    final String signal;
    final String noise;
    final String type;
    final Microseconds offset;
    final Microseconds duration;
    final Integer length;
    final RadioTap radioTap;
    final HTTP http;
    final IP ip;
    final String line;
    final DNS dns;

    private Packet(Builder builder) {
        line      = builder.line;
        timestamp = builder.localTime;
        BSSID     = builder.BSSID;
        DA        = builder.DA;
        RA        = builder.RA;
        SA        = builder.SA;
        TA        = builder.TA;
        signal    = builder.signal;
        noise     = builder.noise;
        offset    = builder.offset;
        duration  = builder.duration;
        length    = builder.length;
        radioTap  = builder.radioTap;
        http      = builder.http;
        ip        = builder.ip;
        dns       = builder.dns;
        type      = builder.type;
    }

    boolean contains(Mac mac) {
        return mac == BSSID || mac == SA || mac == RA || mac == DA || mac == TA;
    }

    boolean contains(Host host) {
        return ip != null && host.equals(ip.source.host) || host.equals(ip.destination.host);
    }

    boolean containsAny(Collection<Host> hosts) {
        for (Host host : hosts) {
            if (contains(host)) {
                return true;
            }
        }
        return false;
    }

    Set<Mac> allMacs() {
        Set<Mac> all = new HashSet(Arrays.asList(new Mac[]{BSSID,SA,RA,DA,TA}));
        all.remove(null);
        return all;
    }

    static class Builder {
        String line;
        Timestamp localTime;
        Mac BSSID;
        Mac DA;
        Mac RA;
        Mac SA;
        Mac TA;
        String signal;
        String noise;
        Microseconds offset;
        Microseconds duration;
        RadioTap radioTap;
        Integer length;
        HTTP http;
        IP ip;
        DNS dns;
        String type;
        String[] dump;

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
        if (timestamp != null) { map.put("timestamp", timestamp.toString());}
        if (BSSID != null)     { map.put("BSSID",BSSID); }
        if (SA != null)        { map.put("SA",SA); }
        if (TA != null)        { map.put("TA",TA); }
        if (RA != null)        { map.put("RA",RA); }
        if (DA != null)        { map.put("DA",DA); }
        if (length != null)    { map.put("length",length); }
        if (duration != null)  { map.put("duration",duration); }
        if (signal != null)    { map.put("signal",signal); }
        if (noise != null)     { map.put("noise",noise); }
        if (offset != null)    { map.put("offset",offset); }
        if (http != null)      { map.put("http",http); }
        if (ip != null)        { map.put("ip",ip); }
        if (dns != null)       { map.put("dns",dns); }
        return "Packet:" + map;
    }

    @Override
    public boolean equals(Object o) {
        Packet that = (Packet) o;
        return  Objects.equals(BSSID,that.BSSID) &&
                Objects.equals(SA,that.SA) &&
                Objects.equals(DA,that.DA) &&
                Objects.equals(RA,that.RA) &&
                Objects.equals(TA,that.TA) &&
                Objects.equals(ip,that.ip) &&
                Objects.equals(timestamp,that.timestamp)
        ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(BSSID,SA,DA,RA,TA,ip, timestamp);
    }
}
